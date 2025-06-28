package tosiltosil.backend.module.auth.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

@RequiredArgsConstructor
public class AuthNumberRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public void save(String email, String authNumber, long expirationTime) {
        redisTemplate.opsForValue().set(email, authNumber, expirationTime);
    }

    public String get(String email) {
        return redisTemplate.opsForValue().get(email);
    }

    public void delete(String email) {
        redisTemplate.delete(email);
    }
}
