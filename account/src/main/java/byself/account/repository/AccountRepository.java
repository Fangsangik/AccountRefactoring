package byself.account.repository;

import byself.account.domain.Account;
import byself.account.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByAccountUser(AccountUser accountUser);

    Integer countByAccountUser(AccountUser accountUser);
    Optional<Account> findByAccountNumber(String accountNumber);

    Optional<Account> findFirstByOrderByIdDesc();
}
