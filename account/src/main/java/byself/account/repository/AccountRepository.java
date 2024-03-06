package byself.account.repository;

import byself.account.domain.Account;
import byself.account.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Integer alreadyCreatedUser(AccountUser accountUser);
}
