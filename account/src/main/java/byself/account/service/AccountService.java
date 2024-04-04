package byself.account.service;

import byself.account.domain.Account;
import byself.account.domain.AccountUser;
import byself.account.dto.AccountDto;

public interface AccountService {

    Account getAccount(Long id);

    void useBalance(Account account, Long amount);

    AccountDto createAccount(Long id, Long initialBalance);
    void validation(AccountUser accountUser, Account account);
    void cancelBalance(Account account, Long amount);

    AccountUser getAccountUser(Long id);
    AccountDto deleteAccount(Long id, String accountNumber);
}
