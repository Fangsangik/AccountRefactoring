package byself.account.controller;

import byself.account.domain.Account;
import byself.account.dto.AccountInfo;
import byself.account.dto.CreateAccount;
import byself.account.dto.DeleteAccount;
import byself.account.service.AccountServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AccountController {
    private AccountServiceImpl accountService;


    @GetMapping("/createAccount")
    public CreateAccount.Response createAccount(@RequestBody @Valid
                                                CreateAccount.Request request) {
        return CreateAccount.Response.from(
                accountService.createAccount(
                        request.getUserId(),
                        request.getInitialBalance()
                )
        );
    }

    @GetMapping("/accountList")
    public List<AccountInfo> getAccountByUserId(
            @RequestParam("userId") Long userId) {
        return accountService.getAccountsByUserId(userId).stream().map(accountDto -> AccountInfo.builder()
                        .accountNumber(accountDto.getAccountNumber())
                        .balance(accountDto.getBalance())
                        .build())
                .collect(Collectors.toList());
    }

    @GetMapping("/account/{id}")
    public Account getAccount(@PathVariable Long id) {
        return accountService.getAccount(id);
    }

    @DeleteMapping("/deleteAccount")
    public DeleteAccount.Response deleteAccount(
            @RequestBody @Valid DeleteAccount.Request request
    ) {
        return DeleteAccount.Response.from(
                accountService.deleteAccount(
                        request.getId(),
                        request.getAccountNumber()
                )
        );
    }
}
