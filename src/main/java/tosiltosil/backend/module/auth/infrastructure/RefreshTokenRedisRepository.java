package tosiltosil.backend.module.auth.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String REFRESH_TOKEN_KEY = "refresh_token:%s";

    public void save(UUID memberId, String refreshToken, long expirationTime) {
        String key = createKey(memberId);
        redisTemplate.opsForValue().set(key, refreshToken, expirationTime, TimeUnit.SECONDS);
    }

    public String get(UUID memberId) {
        String key = createKey(memberId);
        return (String) redisTemplate.opsForValue().get(key);
    }

    public void delete(UUID memberId) {
        String key = createKey(memberId);
        redisTemplate.delete(key);
    }

    private String createKey(UUID memberId) {
        return String.format(REFRESH_TOKEN_KEY, memberId);
    }
}
