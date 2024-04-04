package byself.account.controller;

import byself.account.domain.Account;
import byself.account.service.AccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public class AccountController {
    private AccountService accountService;

    @GetMapping
    public Account getAccount(@PathVariable Long id){
        return accountService.getAccount(id);
    }
}
