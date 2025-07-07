package tosiltosil.backend.module.duration.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tosiltosil.backend.module.duration.domain.DurationRepository;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
class TodayDurationServiceTest {

    @Mock
    private DurationRepository durationRepository;

    @InjectMocks
    private DurationService durationService;

    @Test
    void 사용자의_누적_시간을_업데이트한다() {
        // given
        UUID memberId = UUID.randomUUID();
        Duration currentDuration = Duration.ofHours(3);
        Duration additionalDuration = Duration.ofHours(2);
        Duration expectedTotalDuration = Duration.ofHours(5);
        
        when(durationRepository.findTodayDuration(memberId)).thenReturn(currentDuration);

        // when
        Duration result = durationService.updateTodayDuration(memberId, additionalDuration);

        // then
        assertThat(result).isEqualTo(expectedTotalDuration);
    }

    @Test
    void 초기_누적_시간이_0일때_업데이트한다() {
        // given
        UUID memberId = UUID.randomUUID();
        Duration currentDuration = Duration.ZERO;
        Duration additionalDuration = Duration.ofMinutes(30);
        Duration expectedTotalDuration = Duration.ofMinutes(30);
        
        when(durationRepository.findTodayDuration(memberId)).thenReturn(currentDuration);

        // when
        Duration result = durationService.updateTodayDuration(memberId, additionalDuration);

        // then
        assertThat(result).isEqualTo(expectedTotalDuration);
    }
    
    @Test
    void 차감_결과가_음수일때_ZERO로_설정한다() {
        // given
        UUID memberId = UUID.randomUUID();
        Duration currentDuration = Duration.ofMinutes(30);
        Duration subtractDuration = Duration.ofHours(2);
        Duration expectedTotalDuration = Duration.ZERO;
        
        when(durationRepository.findTodayDuration(memberId)).thenReturn(currentDuration);

        // when
        Duration result = durationService.subtractTodayDuration(memberId, subtractDuration);

        // then
        assertThat(result).isEqualTo(expectedTotalDuration);
        verify(durationRepository).saveTodayDuration(memberId, expectedTotalDuration);
    }
    
    @Test
    void 현재_시간이_ZERO일때_차감해도_ZERO가_유지된다() {
        // given
        UUID memberId = UUID.randomUUID();
        Duration currentDuration = Duration.ZERO;
        Duration subtractDuration = Duration.ofMinutes(30);
        Duration expectedTotalDuration = Duration.ZERO;
        
        when(durationRepository.findTodayDuration(memberId)).thenReturn(currentDuration);

        // when
        Duration result = durationService.subtractTodayDuration(memberId, subtractDuration);

        // then
        assertThat(result).isEqualTo(expectedTotalDuration);
        verify(durationRepository).saveTodayDuration(memberId, expectedTotalDuration);
    }
}