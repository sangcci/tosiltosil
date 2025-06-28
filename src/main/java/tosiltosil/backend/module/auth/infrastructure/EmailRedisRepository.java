package tosiltosil.backend.module.auth.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.UUID;

@RequiredArgsConstructor
public class EmailRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public void save(UUID id, String email, long expirationTime) {
        redisTemplate.opsForValue().set(String.valueOf(id), email, expirationTime);
    }

    public String get(UUID id) {
        return redisTemplate.opsForValue().get(String.valueOf(id));
    }

    public void delete(UUID id) {
        redisTemplate.delete(String.valueOf(id));
    }
}
