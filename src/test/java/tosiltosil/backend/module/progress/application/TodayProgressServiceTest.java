package tosiltosil.backend.module.progress.application;

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
import tosiltosil.backend.module.progress.infrastructure.ProgressRedisRepository;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
class TodayProgressServiceTest {

    @Mock
    private ProgressRedisRepository progressRedisRepository;

    @InjectMocks
    private ProgressService progressService;

    @Test
    void 사용자의_누적_시간을_업데이트한다() {
        // given
        UUID memberId = UUID.randomUUID();
        Duration currentDuration = Duration.ofHours(3);
        Duration additionalDuration = Duration.ofHours(2);
        Duration expectedTotalDuration = Duration.ofHours(5);
        
        when(progressRedisRepository.findTodayDuration(memberId)).thenReturn(currentDuration);

        // when
        progressService.updateTodayDuration(memberId, additionalDuration);

        // then
        Duration result = progressService.getTodayDuration(memberId);
        assertThat(result).isEqualTo(expectedTotalDuration);
    }

    @Test
    void 초기_누적_시간이_0일때_업데이트한다() {
        // given
        UUID memberId = UUID.randomUUID();
        Duration currentDuration = Duration.ZERO;
        Duration additionalDuration = Duration.ofMinutes(30);
        Duration expectedTotalDuration = Duration.ofMinutes(30);
        
        when(progressRedisRepository.findTodayDuration(memberId)).thenReturn(currentDuration);

        // when
        Duration result = progressService.updateTodayDuration(memberId, additionalDuration);

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
        
        when(progressRedisRepository.findTodayDuration(memberId)).thenReturn(currentDuration);

        // when
        Duration result = progressService.subtractTodayDuration(memberId, subtractDuration);

        // then
        assertThat(result).isEqualTo(expectedTotalDuration);
        verify(progressRedisRepository).cacheTodayDuration(memberId, expectedTotalDuration);
    }
    
    @Test
    void 현재_시간이_ZERO일때_차감해도_ZERO가_유지된다() {
        // given
        UUID memberId = UUID.randomUUID();
        Duration currentDuration = Duration.ZERO;
        Duration subtractDuration = Duration.ofMinutes(30);
        Duration expectedTotalDuration = Duration.ZERO;
        
        when(progressRedisRepository.findTodayDuration(memberId)).thenReturn(currentDuration);

        // when
        Duration result = progressService.subtractTodayDuration(memberId, subtractDuration);

        // then
        assertThat(result).isEqualTo(expectedTotalDuration);
        verify(progressRedisRepository).cacheTodayDuration(memberId, expectedTotalDuration);
    }
}