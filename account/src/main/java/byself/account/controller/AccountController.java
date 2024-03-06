package byself.account.controller;

import byself.account.domain.Account;
import byself.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
public class AccountController {

    private final AccountService service;

    @GetMapping("/account/{id}")
    public Account getAccount(
            @PathVariable Long id
    ) {
        return service.getAccount(id);
    }
}
