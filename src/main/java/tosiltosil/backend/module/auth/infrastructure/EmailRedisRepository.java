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

    public void save(UUID id, String email, long expirationTime) {
        redisTemplate.opsForValue().set(String.valueOf(id), email, expirationTime, TimeUnit.SECONDS);
    }

    public String get(UUID id) {
        return (String) redisTemplate.opsForValue().get(String.valueOf(id));
    }

    public void delete(UUID id) {
        redisTemplate.delete(String.valueOf(id));
    }
}
