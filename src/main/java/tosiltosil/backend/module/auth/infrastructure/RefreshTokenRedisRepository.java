package tosiltosil.backend.module.auth.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public void save(UUID memberId, String refreshToken, long expirationTime) {
        redisTemplate.opsForValue().set("refreshToken:" + memberId.toString(), refreshToken, expirationTime);
    }

    public String get(UUID memberId) {
        return redisTemplate.opsForValue().get("refreshToken:" + memberId.toString());
    }

    public void delete(UUID memberId) {
        redisTemplate.delete("refreshToken" + memberId.toString());
    }
}
