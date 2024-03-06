package byself.account.service;

import byself.account.domain.Account;
import byself.account.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AccountServiceTest {

    @Autowired
    AccountRepository accountRepository;

    @Test
    void create_Account(){
        //Given
        Account account = new Account();
        //When
        Account save = accountRepository.save(account);
        //then
        Assertions.assertThat(save).isEqualTo(account);
    }
}