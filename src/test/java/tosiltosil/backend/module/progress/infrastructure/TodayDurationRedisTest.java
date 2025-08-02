package tosiltosil.backend.module.progress.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tosiltosil.backend.support.IntegrationTestSupport;

@SuppressWarnings("NonAsciiCharacters")
public class TodayDurationRedisTest extends IntegrationTestSupport {

    @Autowired
    private ProgressRedisRepository progressRedisRepository;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void setUp() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }
    
    @Test
    void findTodayDuration_존재하지_않는_경우_ZERO_반환() {
        // given
        UUID memberId = UUID.randomUUID();
        
        // when
        Duration result = progressRedisRepository.findTodayDuration(memberId);
        
        // then
        assertThat(result).isEqualTo(Duration.ZERO);
    }
    
    @Test
    void findTodayDuration_ZERO_duration_처리() {
        // given
        UUID memberId = UUID.randomUUID();
        Duration zeroDuration = Duration.ZERO;
        progressRedisRepository.cacheTodayDuration(memberId, zeroDuration);
        
        // when
        Duration result = progressRedisRepository.findTodayDuration(memberId);
        
        // then
        assertThat(result).isEqualTo(Duration.ZERO);
    }
}