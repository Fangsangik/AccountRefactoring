package byself.account.service;

import byself.account.domain.Account;
import byself.account.domain.AccountUser;
import byself.account.domain.Transaction;
import byself.account.dto.TransactionDto;
import byself.account.repository.AccountRepository;
import byself.account.repository.AccountUserRepository;
import byself.account.repository.TransactionRepository;
import byself.account.type.AccountStatus;
import byself.account.type.TransactionType;
import byself.account.type.TransactionalResultType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

import static byself.account.type.TransactionType.*;
import static byself.account.type.TransactionalResultType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountUserRepository accountUserRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountServiceImpl accountService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void successUseBalance(){
        AccountUser accountUser = AccountUser.builder()
                .name("황상익")
                .build();
        accountUser.setId(1L);

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser));

        Account account = Account.builder()
                .accountUser(accountUser)
                .accountStatus(AccountStatus.IN_USER)
                .balance(10000L)
                .accountNumber("123456789")
                .build();

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                        .account(account)
                        .transactionType(USE)
                        .transactionalResultType(S)
                        .transactedAt(LocalDateTime.now())
                        .amount(1000L)
                        .balanceSnapShot(9000L)
                        .build());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        TransactionDto transactionDto = transactionService.useBalance(
                1L, "123456789", 1000L
        );

        verify(transactionRepository, times(1)).save(captor.capture());
        assertEquals(1000L, captor.getValue().getAmount());
        assertEquals(9000L, captor.getValue().getBalanceSnapShot());
        assertEquals(S, transactionDto.getTransactionalResultType());
        assertEquals(USE, transactionDto.getTransactionType());
    }
}