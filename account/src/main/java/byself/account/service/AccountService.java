package byself.account.service;

import byself.account.domain.Account;
import byself.account.domain.AccountUser;
import byself.account.dto.AccountDto;
import org.springframework.stereotype.Service;

@Service
public interface AccountService {

    Account getAccount(Long id);

    void useBalance(Account account, Long amount);

    void validation(AccountUser accountUser, Account account);

    void cancelBalance(Long amount);
}
