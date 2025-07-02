package tosiltosil.backend.module.auth.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class TemporaryTokenRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String TEMPORARY_TOKEN_CACHE_KEY = "temporary_token:%s";

    public void save(String email, String temporaryToken, long expirationTime) {
        String key = createKey(email);
        redisTemplate.opsForValue().set(key, temporaryToken, expirationTime, TimeUnit.SECONDS);
    }

    public String get(String email) {
        String key = createKey(email);
        return (String) redisTemplate.opsForValue().get(key);
    }

    public void delete(String email) {
        String key = createKey(email);
        redisTemplate.delete(key);
    }

    private String createKey(String email) {
        return String.format(TEMPORARY_TOKEN_CACHE_KEY, email);
    }
}
