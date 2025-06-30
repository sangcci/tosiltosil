package tosiltosil.backend.module.auth.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class AuthNumberRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public void save(String email, String authNumber, long expirationTime) {
        redisTemplate.opsForValue().set(email, authNumber, expirationTime, TimeUnit.SECONDS);
    }

    public String get(String email) {
        return (String) redisTemplate.opsForValue().get(email);
    }

    public void delete(String email) {
        redisTemplate.delete(email);
    }
}
