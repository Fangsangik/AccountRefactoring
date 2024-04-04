package byself.account.service;


import byself.account.domain.Account;
import byself.account.domain.AccountUser;
import byself.account.dto.AccountDto;
import byself.account.exception.AccountException;
import byself.account.repository.AccountRepository;
import byself.account.repository.AccountUserRepository;
import byself.account.type.AccountStatus;
import byself.account.type.ErrorCode;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static byself.account.type.AccountStatus.*;
import static byself.account.type.ErrorCode.*;
import static byself.account.type.ErrorCode.NOT_ENOUGH_MONEY;

@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountUserRepository accountUserRepository;

    @Override
    public Account getAccount(Long id) {
        if (id < 0) {
            throw new IllegalStateException(String.format("잘못된 값입니다."));
        }
        return accountRepository.findById(id).get();
    }

    @Override
    public AccountDto createAccount(Long id, Long initialBalance) {
        AccountUser accountUser = getAccountUser(id);

        validationCreate(accountUser);

        String newAccountNumber = accountRepository.findFirstByOrderByIdDesc()
                .map(account -> (Integer.parseInt(account.getAccountNumber())) + 1 + "")
                .orElse("100000000");

        return AccountDto.fromEntity(
                accountRepository.save(
                        Account.builder()
                                .accountUser(accountUser)
                                .accountStatus(IN_USER)
                                .accountNumber(newAccountNumber)
                                .balance(initialBalance)
                                .registeredAt(LocalDateTime.now())
                                .build()
                )
        );
    }

    public void validationCreate(AccountUser accountUser) {
        if (accountRepository.countByAccountUser(accountUser) >= 2) {
            throw new AccountException(ErrorCode.MAX_ACCOUNT_PER_USER);
        }
    }

    @Override
    public AccountDto deleteAccount(Long id, String accountNumber) {
        AccountUser accountUser = getAccountUser(id);

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

        validation(accountUser, account);
        account.setAccountStatus(UNREGISTERED);
        account.setUnregisteredAt(LocalDateTime.now());

        return AccountDto.fromEntity(account);
    }

    @Override
    public void useBalance(Account account, Long amount) {
        if (amount > account.getBalance()) {
            throw new AccountException(NOT_ENOUGH_MONEY);
        } else {
            Long curBalance = account.getBalance();
            account.setBalance(curBalance - amount);
        }
    }

    @Override
    public void validation(AccountUser accountUser, Account account) {
        if (!accountUser.getId().equals(account.getId())) {
            throw new AccountException(ID_NOT_MATCH);
        }

        if (account.getAccountStatus() == UNREGISTERED) {
            throw new AccountException(ErrorCode.ALREADY_UNREGISTERED);
        }

        if (account.getBalance() > 0) {
            throw new AccountException(ErrorCode.BALANCE_LEFT_MONEY);
        }
    }

    @Override
    public void cancelBalance(Account account, Long amount) {
        if (amount < 0) {
            throw new AccountException(INVALID_REQUEST);
        } else {
            Long curBalance = account.getBalance();
            account.setBalance(curBalance + amount);
        }
    }

    @Override
    public AccountUser getAccountUser(Long id) {
        return accountUserRepository.findById(id)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));
    }
}
