package byself.account.service;


import byself.account.domain.Account;
import byself.account.domain.AccountUser;
import byself.account.dto.AccountDto;
import byself.account.exception.AccountException;
import byself.account.repository.AccountRepository;
import byself.account.repository.AccountUserRepository;
import byself.account.type.AccountStatus;
import byself.account.type.ErrorCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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

    @Test
    @DisplayName("계좌 생성 실패")
    void createAccount_Fail() {
        AccountUser user = new AccountUser().builder()
                .name("Hwang")
                .build();
        user.setId(2L);
//        given(accountRepository.findById(anyLong()))
//                .willReturn(Optional.empty());

        AccountException accountException = assertThrows(AccountException.class,
                () -> accountService.createAccount(1L, 10000L));

        assertEquals(ErrorCode.USER_NOT_FOUND, accountException.getErrorCode());
    }

    @Test
    @DisplayName("생성 계좌는 총 2개")
    void creatAccount_max2() {
        AccountUser accountUser = AccountUser.builder()
                .name("Hwang")
                .build();
        accountUser.setId(1L);
        accountUser.setCreatedAt(LocalDateTime.now());

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser));

        given(accountRepository.countByAccountUser(any()))
                .willReturn(3);

        AccountException accountException = assertThrows(AccountException.class,
                () -> accountService.createAccount(1L, 10000L));
        assertEquals(ErrorCode.MAX_ACCOUNT_PER_USER, accountException.getErrorCode());
    }

    @Test
    @DisplayName("계좌 삭제")
    void deleteAccount() {
        AccountUser user = AccountUser.builder()
                .name("Park")
                .build();
        user.setId(1L);

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(user)
                        .accountNumber("123456789")
                        .balance(0L)
                        .build()));

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        //when
        AccountDto accountDto = accountService.deleteAccount(1L, "123456789");

        //then
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(1L, accountDto.getId());
        assertEquals("123456789", captor.getValue().getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED, captor.getValue().getAccountStatus());
    }


    @Test
    @DisplayName("해당 유저 X -> 계좌 해지 실패")
    void deleteAccount_Fail() {

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        AccountException accountException = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L, "123456789"));

        assertEquals(ErrorCode.USER_NOT_FOUND, accountException.getErrorCode());
    }

    @Test
    @DisplayName("해당 userX")
    void deleteAccount_UserNotFound() {
        AccountUser user = AccountUser.builder()
                .name("Hwang")
                .build();
        user.setId(1L);

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());

        AccountException accountException = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L, "123456789"));

        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, accountException.getErrorCode());
    }

    @Test
    @DisplayName("계좌 소유주 다름")
    void deleteAccount_userNotMatch() {
        AccountUser accountUser1 = AccountUser.builder()
                .name("Hwang")
                .build();
        accountUser1.setId(1L);

        AccountUser accountUser2 = AccountUser.builder()
                .name("Park")
                .build();
        accountUser2.setId(2L);

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser1));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(accountUser2)
                        .accountNumber("123456789")
                        .balance(0L)
                        .build()));

        AccountException accountException = assertThrows(AccountException.class
                , () -> accountService.deleteAccount(1L, "987654321"));

        assertEquals(ErrorCode.ID_NOT_MATCH, accountException.getErrorCode());
    }

    @Test
    void balance_NotEmpty() {
        AccountUser accountUser1 = AccountUser.builder()
                .name("Hwang")
                .build();
        accountUser1.setId(1L);

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser1));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountNumber("123456789")
                        .accountUser(accountUser1)
                        .balance(1000L)
                        .build()));

        AccountException accountException = assertThrows(AccountException.class
                , () -> accountService.deleteAccount(1L, "1234566789"));

        assertEquals(ErrorCode.BALANCE_LEFT_MONEY, accountException.getErrorCode());
    }

    @Test
    void alreadyDeletedAccount() {
        AccountUser accountUser1 = AccountUser.builder()
                .name("Hwang")
                .build();
        accountUser1.setId(1L);

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser1));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(accountUser1)
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .accountNumber("123456789")
                .balance(0L)
                .build()));

        AccountException accountException = assertThrows(AccountException.class
                , () -> accountService.deleteAccount(1L, "1234566789"));

        assertEquals(ErrorCode.ALREADY_UNREGISTERED, accountException.getErrorCode());
    }

    @Test
    void successGetAccountByUserId(){
        AccountUser Pobi = AccountUser.builder()
                .name("Pobi")
                .build();
        Pobi.setId(1L);

        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(Pobi));

        List<Account> accounts = Arrays.asList(
                Account.builder()
                        .accountUser(Pobi)
                        .accountNumber("1111111")
                        .balance(1000L)
                        .build(),
                Account.builder()
                        .accountUser(Pobi)
                        .accountNumber("222222")
                        .balance(2000L)
                        .build(),
                Account.builder()
                        .accountUser(Pobi)
                        .accountNumber("333333")
                        .balance(3000L)
                        .build()
        );

        given(accountRepository.findByAccountUser(any()))
                .willReturn(accounts);

        List<AccountDto> accountDtos = accountService.getAccountsByUserId(1L);

        assertEquals(3, accountDtos.size());
        assertEquals("1111111", accountDtos.get(0).getAccountNumber());
        assertEquals(1000, accountDtos.get(0).getBalance());
        assertEquals("222222", accountDtos.get(1).getAccountNumber());
        assertEquals(2000, accountDtos.get(1).getBalance());
        assertEquals("333333", accountDtos.get(2).getAccountNumber());
        assertEquals(3000, accountDtos.get(2).getBalance());
    }

    @Test
    void failedToGetAccounts(){
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.getAccountsByUserId(1L));

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

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
    void findAccount_Fail(){
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> accountService.getAccount(-1L));

        assertEquals("잘못된 값입니다.", exception.getMessage());
    }

    @Test
    void testSuccess(){
        given(accountRepository.findById(any()))
                .willReturn(Optional.of(Account.builder()
                        .accountStatus(AccountStatus.IN_USER)
                        .accountNumber("123456789")
                        .build()));

        Account account = accountService.getAccount(1L);

        assertEquals("123456789", account.getAccountNumber());
        assertEquals(AccountStatus.IN_USER, account.getAccountStatus());
    }
}
