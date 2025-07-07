package tosiltosil.backend.module.email.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import tosiltosil.backend.module.email.domain.EmailAuthMeta;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class EmailAuthRedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String EMAIL_AUTH_CNT_KEY = "email_auth_cnt:%s";

    public void save(UUID clientId, int sendCount, int authCount, long expirationTime) {
        String key = createKey(clientId);
        EmailAuthMeta emailAuthMeta = new EmailAuthMeta(sendCount, authCount);

        redisTemplate.opsForValue().set(key, emailAuthMeta, expirationTime, TimeUnit.SECONDS);
    }

    public EmailAuthMeta get(UUID clientId) {
        String key = createKey(clientId);
        return (EmailAuthMeta) redisTemplate.opsForValue().get(key);
    }

    public void delete(UUID clientId) {
        String key = createKey(clientId);
        redisTemplate.delete(key);
    }

    public void increaseSendCount(UUID clientId) {
        EmailAuthMeta emailAuthMeta = get(clientId);
        int sendCount = emailAuthMeta.sendCount();

        updateWithPreservingTtl(clientId, sendCount + 1, emailAuthMeta.authFailCount());
    }

    public void increaseFailCount(UUID clientId) {
        EmailAuthMeta emailAuthMeta = get(clientId);
        int failCount = emailAuthMeta.authFailCount();

        updateWithPreservingTtl(clientId, emailAuthMeta.sendCount(), failCount + 1);
    }

    private void updateWithPreservingTtl(UUID clientId, int sendCount, int failCount) {
        String key = createKey(clientId);
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);

        save(clientId, sendCount, failCount, ttl);
    }

    private String createKey(UUID clientId) {
        return String.format(EMAIL_AUTH_CNT_KEY, clientId.toString());
    }
}
