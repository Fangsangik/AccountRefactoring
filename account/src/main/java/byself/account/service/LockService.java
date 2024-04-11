package byself.account.service;

import byself.account.exception.AccountException;
import byself.account.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class LockService {
    private final RedissonClient client;

    public void lock(String accountNumber){
        RLock lock = client.getLock(getLockKey(accountNumber));
        log.debug("Trying lock for accountNumber : {}", accountNumber);

        try {
            boolean isLock = lock.tryLock(1, 20, TimeUnit.SECONDS);
            if (!isLock) {
                log.error("Lock acqusition failed");
                throw new AccountException(ErrorCode.ACCOUNT_TRANSACTION_LOCK);
            }
        } catch (AccountException e) {
            throw e;
        } catch (Exception e){
            log.error("lock failed", e);
        }
    }

    private String getLockKey(String accountNumber) {
        return "ACKL : " + accountNumber;
    }

    private void unLock(String accountNumber){
        log.debug("Unlock for accountNumber : {}", accountNumber);
        client.getLock(getLockKey(accountNumber)).unlock();
    }
}
