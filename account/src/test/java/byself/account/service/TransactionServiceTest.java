package byself.account.service;

import byself.account.domain.Account;
import byself.account.domain.AccountUser;
import byself.account.domain.Transaction;
import byself.account.dto.TransactionDto;
import byself.account.exception.AccountException;
import byself.account.repository.AccountRepository;
import byself.account.repository.AccountUserRepository;
import byself.account.repository.TransactionRepository;
import byself.account.type.AccountStatus;
import byself.account.type.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.Optional;

import static byself.account.type.TransactionType.USE;
import static byself.account.type.TransactionalResultType.F;
import static byself.account.type.TransactionalResultType.S;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TransactionServiceTest {
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
    void successUseBalance() {
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
        assertEquals(10000L, captor.getValue().getBalanceSnapShot());
        assertEquals(S, transactionDto.getTransactionalResultType());
        assertEquals(USE, transactionDto.getTransactionType());
    }

    @Test
    void deleteAccount_UserNotFound() {
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        AccountException accountException = assertThrows(AccountException.class, () ->
                transactionService.useBalance(1L, "123456789", 10000L));

        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, accountException.getErrorCode());
    }

    @Test
    void deleteAccount_AccountNotFound() {
        AccountUser accountUser = AccountUser.builder()
                .name("황상익")
                .build();
        accountUser.setId(1L);

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());

        AccountException accountException = assertThrows(AccountException.class, ()
                -> transactionService.useBalance(1L, "123456789", 10000L));

        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, accountException.getErrorCode());
    }

    @Test
    void deleteAccountFailed_userUnMatch() {
        AccountUser accountUser1 = AccountUser.builder()
                .name("박주연")
                .build();
        accountUser1.setId(1L);

        AccountUser accountUser2 = AccountUser.builder()
                .name("황상익")
                .build();
        accountUser2.setId(2L);

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser1));


        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(accountUser2)
                        .accountNumber("123456789")
                        .balance(0L)
                        .accountStatus(AccountStatus.IN_USER)
                        .build()));

        AccountException accountException = assertThrows(AccountException.class, ()
                -> transactionService.useBalance(1L, "123456789", 10000L));

        assertEquals(ErrorCode.ID_NOT_MATCH, accountException.getErrorCode());
    }

    @Test
    void alreadyUnregistered() {
        //given
        AccountUser accountUser1 = AccountUser.builder()
                .name("황상익")
                .build();
        accountUser1.setId(1L);

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser1));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(accountUser1)
                        .balance(0L)
                        .unregisteredAt(LocalDateTime.now())
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .accountNumber("1234567890")
                        .build()));

        AccountException accountException = assertThrows(AccountException.class, () -> transactionService.useBalance(1L, "123456789", 10000L));

        assertEquals(ErrorCode.USER_NOT_FOUND, accountException.getErrorCode());
    }

    @Test
    void exceedAmount_useBalance() {
        AccountUser accountUser1 = AccountUser.builder()
                .name("황상익")
                .build();
        accountUser1.setId(1L);

        Account account = Account.builder()
                .accountUser(accountUser1)
                .accountStatus(AccountStatus.IN_USER)
                .balance(100L)
                .accountNumber("123456789")
                .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser1));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        AccountException accountException = assertThrows(AccountException.class, ()
                -> transactionService.useBalance(1L, "123456789", 10000L));

        assertEquals(ErrorCode.AMOUNT_EXCEED, accountException.getErrorCode());
    }

    @Test
    void saveFailedUseTransaction() {
        //given
        AccountUser accountUser = AccountUser.builder()
                .name("황상익")
                .build();
        accountUser.setId(1L);

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser));

        Account account = Account.builder()
                .accountNumber("123456789")
                .accountUser(accountUser)
                .accountStatus(AccountStatus.IN_USER)
                .balance(10000L)
                .build();

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                        .account(account)
                        .transactionType(USE)
                        .transactionalResultType(S)
                        .transactionId("Hello")
                        .amount(10000L)
                        .balanceSnapShot(9000L)
                        .build());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        //when
        transactionService.saveFailedUseTransaction("123456789", 1000L);

        //then
        verify(transactionRepository, times(1)).save(captor.capture());
        assertEquals(1000L, captor.getValue().getAmount());
        assertEquals(10000L, captor.getValue().getBalanceSnapShot());
        assertEquals(F, captor.getValue().getTransactionalResultType());
    }
}

