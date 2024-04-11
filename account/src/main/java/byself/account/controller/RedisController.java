package byself.account.controller;

import byself.account.aop.AccountLockInterface;
import byself.account.service.LockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

@Service
@RequiredArgsConstructor
public class RedisController {
    private final LockService lockService;

    private final AccountLockInterface accountLockInterface;

    @GetMapping("/get-lock")
    public String getLock(){
        lockService.lock(accountLockInterface.getAccountNumber());
        return "success";
    }
}
