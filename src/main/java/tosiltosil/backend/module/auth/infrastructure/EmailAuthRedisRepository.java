package tosiltosil.backend.module.auth.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import tosiltosil.backend.module.auth.domain.response.email.EmailAuthMeta;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class EmailAuthRedisRepository {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String EMAIL_AUTH_CNT_KEY = "email_auth_cnt:%s";
    private static final String SEND_COUNT_FIELD = "sendCount";
    private static final String AUTH_FAIL_COUNT_FIELD = "authFailCount";

    public void save(String email, int sendCount, int authFailCount, long expirationTime) {
        String key = createKey(email);
        redisTemplate.opsForHash().put(key, SEND_COUNT_FIELD, String.valueOf(sendCount));
        redisTemplate.opsForHash().put(key, AUTH_FAIL_COUNT_FIELD, String.valueOf(authFailCount));
        redisTemplate.expire(key, expirationTime, TimeUnit.SECONDS);
    }

    public EmailAuthMeta get(String email) {
        String key = createKey(email);

        if (!redisTemplate.hasKey(key)) {
            return null;
        }

        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);

        int sendCount = parseOrDefault(entries.get(SEND_COUNT_FIELD));
        int authFailCount = parseOrDefault(entries.get(AUTH_FAIL_COUNT_FIELD));

        return new EmailAuthMeta(sendCount, authFailCount);
    }

    public void delete(String email) {
        String key = createKey(email);
        redisTemplate.delete(key);
    }

    public void increaseSendCount(String email) {
        String key = createKey(email);
        redisTemplate.opsForHash().increment(key, SEND_COUNT_FIELD, 1);
    }

    public int increaseAuthFailCount(String email) {
        String key = createKey(email);
        Long increasedAuthFailCount = redisTemplate.opsForHash().increment(key, AUTH_FAIL_COUNT_FIELD, 1);
        return increasedAuthFailCount.intValue();
    }

    private String createKey(String email) {
        return String.format(EMAIL_AUTH_CNT_KEY, email);
    }

    private int parseOrDefault(Object value) {
        if (value == null) {
            return 0;
        }
        try {
            return Integer.parseInt((String) value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
