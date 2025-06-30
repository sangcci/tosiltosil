package tosiltosil.backend.module.auth.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class EmailRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String EMAIL_CACHE_KEY = "email_cache_key:%s";

    public void save(UUID id, String email, long expirationTime) {
        String key = createKey(id);
        redisTemplate.opsForValue().set(key, email, expirationTime, TimeUnit.SECONDS);
    }

    public String get(UUID id) {
        String key = createKey(id);
        return (String) redisTemplate.opsForValue().get(key);
    }

    public void delete(UUID id) {
        String key = createKey(id);
        redisTemplate.delete(key);
    }

    private String createKey(UUID id) {
        return String.format(EMAIL_CACHE_KEY, id);
    }
}
