package byself.account.service;


import byself.account.domain.Account;
import byself.account.domain.AccountUser;
import byself.account.dto.AccountDto;
import byself.account.repository.AccountRepository;
import byself.account.repository.AccountUserRepository;
import byself.account.type.AccountStatus;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

//@SpringBootTest
@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountUserRepository accountUserRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void findAccoount() {
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.of(Account.builder()
                        .accountStatus(AccountStatus.IN_USER)
                        .accountNumber("123456789")
                        .build()));

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        Account account = accountService.getAccount(1L);

        verify(accountRepository, times(1))
                .findById(captor.capture());
        verify(accountRepository, times(0)).save(any());
        assertEquals(1L, captor.getValue());
        // assertEquals(2L, captor.getValue());
        assertEquals("123456789", account.getAccountNumber());

    }

    @Test
    void createAccount() {
        // given
        AccountUser accountUser = AccountUser.builder()
                .name("Hwang")
                .build();
        accountUser.setId(1L);

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser));

        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.of(Account.builder()
                        .accountNumber("123456789")
                        .build()));

        given(accountRepository.save(any()))
                .willAnswer(invocation -> {
                    Account savedAccount = invocation.getArgument(0);
                    savedAccount.setId(1L); // 가짜 계정에 ID 설정
                    savedAccount.setAccountNumber("12345678"); // 가짜 계정에 계정 번호 설정
                    return savedAccount;
                });

        // when
        AccountDto accountDto = accountService.createAccount(1L, 10000L);

        // then
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository, times(1)).save(captor.capture());

        assertEquals(1L, accountDto.getId());
        assertEquals("12345678", captor.getValue().getAccountNumber());
    }

    @Test
    @DisplayName("성공")
    void createFirstAccount() {
        // given
        AccountUser accountUser = AccountUser.builder()
                .name("Hwang")
                .build();

        accountUser.setId(1L);

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser));

        // 첫 번째 계정이 없을 경우를 가정하여 Optional.empty()가 아닌 null을 반환하도록 설정
        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.empty());

        given(accountRepository.save(any()))
                .willAnswer(invocation -> {
                    Account savedAccount = invocation.getArgument(0);
                    savedAccount.setId(1L); // 가짜 계정에 ID 설정
                    savedAccount.setAccountNumber("100000000"); // 가짜 계정에 계정 번호 설정
                    return savedAccount;
                });

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        // when
        AccountDto accountDto = accountService.createAccount(1L, 50000L);

        // then
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(1L, accountDto.getId());
        assertEquals("100000000", captor.getValue().getAccountNumber());
    }
}
