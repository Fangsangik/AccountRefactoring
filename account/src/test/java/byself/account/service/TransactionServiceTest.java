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
<<<<<<< HEAD
import byself.account.type.TransactionType;
import byself.account.type.TransactionalResultType;
import org.junit.jupiter.api.Assertions;
=======
>>>>>>> 4be8550e09fba9a44d1554aba02a6224d40c6648
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
<<<<<<< HEAD
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
=======
>>>>>>> 4be8550e09fba9a44d1554aba02a6224d40c6648

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
<<<<<<< HEAD

    @Test
    void successCancelBalance(){
        AccountUser accountUser = AccountUser.builder()
                .name("황상익")
                .build();
        accountUser.setId(1L);
        Account account = Account.builder()
                .accountUser(accountUser)
                .accountStatus(AccountStatus.IN_USER)
                .balance(100000L)
                .accountNumber("1000000012")
                .build();

        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(USE)
                .transactionalResultType(S)
                .transactionId("transactionId")
                .transactedAt(LocalDateTime.now())
                .amount(1000L)
                .balanceSnapShot(9000L)
                .build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(accountUser));


        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));

        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                        .account(account)
                        .transactionType(USE)
                        .transactionalResultType(S)
                        .transactionId("transactionIdForCancel")
                        .transactedAt(LocalDateTime.now())
                        .amount(1000L)
                        .balanceSnapShot(10000L)
                        .build());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        //when
        TransactionDto transactionDto = transactionService
                .cancelBalance("transactionId", "100000000", 1000L);

        //then
        verify(transactionRepository, times(1)).save(captor.capture());
        assertEquals(1000L, captor.getValue().getAmount());
        assertEquals(10000L + 1000L, captor.getValue().getBalanceSnapShot());
        assertEquals(S, transactionDto.getTransactionType());
        assertEquals(CANCEL, transactionDto.getTransactionType());
        assertEquals(10000L, transactionDto.getBalanceSnapShot());
        assertEquals(1000L, transactionDto.getAmount());
    }

    @Test
    void CancelAmount_AccountNotFound(){
        given(transactionRepository.findById(anyLong()))
                .willReturn(Optional.of(Transaction.builder()
                        .build()));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());

        AccountException accountException = assertThrows(AccountException.class,
                () -> transactionService.cancelBalance("Id", "123456789", 1000L));

        assertEquals(ErrorCode.ID_NOT_MATCH, accountException.getErrorCode());
    }

    @Test
    @DisplayName("거래와 취소 금액 X - 잔액사용 취소 실패")
    void cancelAccount_CancelMustFully() {
        AccountUser user = AccountUser.builder()
                .name("Nana")
                .build();
        user.setId(12L);

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(AccountStatus.IN_USER)
                .balance(100000L)
                .accountNumber("1000000012")
                .build();
        account.setId(1L);

        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(USE)
                .transactionalResultType(S)
                .transactionId("transactionId")
                .transactedAt(LocalDateTime.now())
                .amount(1000L + 1000L)
                .balanceSnapShot(9000L)
                .build();

        //given
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        //when
        AccountException exception = assertThrows(AccountException.class, ()
                -> transactionService
                .cancelBalance("transactionId", "10000000000", 1000L));

        //then

        assertEquals(ErrorCode.CANCEL_MUST_FULLY, exception.getErrorCode());
    }

    @Test
    @DisplayName("취소는 1년까지만 가능 - 잔액사용 취소 실패")
    void cancelAccount_TooOldOrder() {
        AccountUser user = AccountUser.builder()
                .name("Nana")
                .build();
        user.setId(12L);

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(AccountStatus.IN_USER)
                .balance(100000L)
                .accountNumber("1000000012")
                .build();
        account.setId(1L);

        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(USE)
                .transactionalResultType(S)
                .transactionId("transactionId")
                .transactedAt(LocalDateTime.now().minusYears(1))
                .amount(1000L)
                .balanceSnapShot(9000L)
                .build();

        //given
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        //when
        AccountException exception = assertThrows(AccountException.class, ()
                -> transactionService
                .cancelBalance("transactionId", "10000000000", 1000L));

        //then

        assertEquals(ErrorCode.TOO_OLD_TO_CNACEL, exception.getErrorCode());
    }

    @Test
    void successQueryTransaction() {
        AccountUser user = AccountUser.builder()
                .name("Nana")
                .build();
        user.setId(12L);

        Account account = Account.builder()
                .accountUser(user)
                .accountStatus(AccountStatus.IN_USER)
                .balance(100000L)
                .accountNumber("1000000012")
                .build();
        account.setId(1L);

        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(USE)
                .transactionalResultType(S)
                .transactionId("transactionId")
                .transactedAt(LocalDateTime.now().minusYears(1))
                .amount(1000L)
                .balanceSnapShot(9000L)
                .build();

        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));

        TransactionDto transactionDto = transactionService.queryTransacation("trxId");
        assertEquals(USE, transactionDto.getTransactionType());
        assertEquals(S, transactionDto.getTransactionalResultType());
        assertEquals(1000L, transactionDto.getAmount());
        assertEquals("transactionId", transactionDto.getTransactionId());
    }

    @Test
    @DisplayName("원 거래 없음 - 거래 조회 실패")
    void queryTransaction_TransactionNotFound() {
        //given
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.empty());

        //when
        AccountException exception = assertThrows(AccountException.class, ()
                -> transactionService.queryTransacation("transactionId"));

        //then

        assertEquals(ErrorCode.TRANSACTION_NOT_FOUND, exception.getErrorCode());
    }
}
=======
}

>>>>>>> 4be8550e09fba9a44d1554aba02a6224d40c6648
