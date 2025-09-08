package tosiltosil.backend.module.progress.infrastructure;

import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProgressRedisRepository {

    private static final String TOTAL_TIME_KEY = "today_total_time:%s";

    private final RedisTemplate<String, Object> redisTemplate;

    private String createKey(final UUID memberId) {
        return String.format(TOTAL_TIME_KEY, memberId);
    }

    public void cacheTodayDuration(final UUID memberId, final Duration duration) {
        String key = createKey(memberId);
        redisTemplate.opsForValue().set(key, duration.toSeconds());
    }

    public Duration findTodayDuration(final UUID memberId) {
        String key = createKey(memberId);
        Object duration = redisTemplate.opsForValue().get(key);
        if (duration == null) {
            return Duration.ZERO;
        }
        return Duration.ofSeconds(((Number) duration).longValue());
    }
}
