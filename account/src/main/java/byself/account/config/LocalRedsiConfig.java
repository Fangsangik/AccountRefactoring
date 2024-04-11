package byself.account.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import redis.embedded.RedisServer;

public class LocalRedsiConfig {

    @Value("${spring.redis.port}")
    private int redisProt;

    private RedisServer redisServer;

    @PostConstruct
    public void startRedis(){
        redisServer = new RedisServer(redisProt);
        redisServer.start();
    }

    @PreDestroy
    public void endRedis(){
        if (redisServer != null){
            redisServer.stop();
        }
    }
}
