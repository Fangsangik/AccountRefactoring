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
import byself.account.type.TransactionType;
import byself.account.type.TransactionalResultType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static byself.account.type.ErrorCode.*;
import static byself.account.type.TransactionType.*;
import static byself.account.type.TransactionalResultType.*;

@Slf4j
@Service
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService{

    @Autowired
    private final TransactionRepository transactionRepository;
    @Autowired
    private final AccountUserRepository accountUserRepository;
    @Autowired
    private final AccountRepository accountRepository;
    @Autowired
    private final AccountServiceImpl accountService;

    @Override
    @Transactional
    public TransactionDto useBalance(Long userId, String accountNumber, Long amount) {
        try {
            AccountUser accountUser = accountUserRepository.findById(userId)
                    .orElseThrow(() -> new AccountException(USER_NOT_FOUND));

            Account account = accountRepository.findByAccountNumber(accountNumber)
                    .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

            validateUserBalance(accountUser, account, amount);

            return TransactionDto.fromEntity(saveAndGetTransaction(USE, S, account, amount));
        } catch (AccountException e) {
            saveFailedUseTransaction(accountNumber, amount);
            throw e;
        }
    }

    public void validateUserBalance(AccountUser accountUser, Account account, Long amount){
        if (!Objects.equals(accountUser.getId(), account.getAccountUser().getId())) {
            throw new AccountException(ID_NOT_MATCH);
        }

        if (account.getAccountStatus() != AccountStatus.IN_USER){
            throw new AccountException(USER_NOT_FOUND);
        }

        if (account.getBalance() < amount){
            throw new AccountException(AMOUNT_EXCEED);
        }
    }

    @Override
    public void saveFailedUseTransaction(String accountNumber, Long amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

        saveAndGetTransaction(USE, F, account, amount);
    }

    private Transaction saveAndGetTransaction (TransactionType transactionType,
                                               TransactionalResultType transactionalResultType,
                                               Account account,
                                               Long amount) {
        return transactionRepository.save(
                Transaction.builder()
                        .transactionType(transactionType)
                        .transactionalResultType(F)
                        .account(account)
                        .amount(amount)
                        .balanceSnapShot(account.getBalance())
                        .transactionId(UUID.randomUUID().toString().replace("-", ""))
                        .transactedAt(LocalDateTime.now())
                        .build()
        );
    }

    @Override
    @Transactional
    public TransactionDto cancelBalance(String transactionId, String accountNumber, Long amount) {
        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new AccountException(ID_NOT_MATCH));

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

        validateCancleBalance(transaction, account, amount);

        accountService.cancelBalance(amount);

        return TransactionDto.fromEntity(
                saveAndGetTransaction(CANCEL, S, account, amount)
        );
    }

    private void validateCancleBalance(Transaction transaction,
                                       Account account,
                                       Long amount){
        if (!Objects.equals(transaction.getAccount().getId(), account.getId())){
            throw new AccountException(TRANSACTION_ACCOUNT_UNMATCH);
        }

        if (transaction.getAccount() != account){
            throw new AccountException(CANCEL_MUST_FULLY);
        }

        if (transaction.getTransactedAt().isBefore(LocalDateTime.now())){
            throw new AccountException(ErrorCode.TOO_OLD_TO_CNACEL);
        }
    }

    @Override
    public void saveFailedCancelTransaction(String accountNumber, Long amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));
        saveAndGetTransaction(CANCEL, F, account, amount);
    }

    @Override
    public TransactionDto queryTransacation(String transactionId) {
        return TransactionDto.fromEntity(
                transactionRepository.findByTransactionId(transactionId)
                        .orElseThrow(() -> new AccountException(ErrorCode.TRANSACTION_NOT_FOUND))
        );
    }
}
