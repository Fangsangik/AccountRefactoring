package byself.account.service;

import byself.account.exception.AccountException;
import byself.account.type.ErrorCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

import java.lang.annotation.Inherited;

@ExtendWith(MockitoExtension.class)
public class LockServiceTest {

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RLock rLock;

    @InjectMocks
    private LockService lockService;

    @Test
    void successLock() throws InterruptedException {
        given(redissonClient.getLock(anyString()))
                .willReturn(rLock);

        given(rLock.tryLock(anyLong(), anyLong(), any()))
                .willReturn(true);

        Assertions.assertDoesNotThrow(() -> lockService.lock("123"));
    }

    @Test
    void failedLock() throws InterruptedException {
        given(redissonClient.getLock(any()))
                .willReturn(rLock);

        given(rLock.tryLock(anyLong(), anyLong(), any()))
                .willReturn(false);

        AccountException exception = assertThrows(AccountException.class, ()
                -> lockService.lock("123"));

        assertEquals(ErrorCode.ACCOUNT_TRANSACTION_LOCK, exception.getErrorCode());
    }
}
