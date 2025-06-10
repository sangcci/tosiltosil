package tosiltosil.backend.module.goal.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@SuppressWarnings("NonAsciiCharacters")
class DailyTotalTimeTest {

    @Test
    void 사용자의_하루_총_누적_시간이_24시간_이후라면_false를_반환한다() {
        // Given
        DailyTotalTime dailyTotalTime = DailyTotalTime.of(UUID.randomUUID());
        Duration currentTime = Duration.ofHours(24).plusMinutes(1);
        ReflectionTestUtils.setField(dailyTotalTime, "time", currentTime);
        
        // When
        boolean result = dailyTotalTime.validateDurationUnder24Hours();
        
        // Then
        assertThat(result).isFalse();
    }

    @Test
    void 사용자의_하루_총_누적_시간이_24시간_이내라면_true를_반환한다() {
        // Given
        DailyTotalTime dailyTotalTime = DailyTotalTime.of(UUID.randomUUID());
        Duration currentTime = Duration.ofHours(23).plusMinutes(59);
        ReflectionTestUtils.setField(dailyTotalTime, "time", currentTime);
        
        // When
        boolean result = dailyTotalTime.validateDurationUnder24Hours();
        
        // Then
        assertThat(result).isTrue();
    }

    @Test
    void 사용자의_하루_총_누적_시간이_정확히_24시간이라면_false를_반환한다() {
        // Given
        DailyTotalTime dailyTotalTime = DailyTotalTime.of(UUID.randomUUID());
        Duration currentTime = Duration.ofHours(24);
        ReflectionTestUtils.setField(dailyTotalTime, "time", currentTime);
        
        // When
        boolean result = dailyTotalTime.validateDurationUnder24Hours();
        
        // Then
        assertThat(result).isFalse();
    }
}