package byself.account.repository;

import byself.account.domain.Account;
import byself.account.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query(name = "AccountUser.findByUserName")
    List<AccountUser> findByUserName(@Param("name") String name);

    Integer countByAccountUser(AccountUser accountUser);
    Optional<Account> findByAccountNumber(String accountNumber);

    Optional<Account> findFirstByOrderByIdDesc();
}
