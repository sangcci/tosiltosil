package tosiltosil.backend.module.goal.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tosiltosil.backend.common.domain.exception.NotFoundException;
import tosiltosil.backend.module.goal.domain.Goal;
import tosiltosil.backend.module.goal.domain.GoalRepository;
import tosiltosil.backend.module.goal.domain.value.GoalStatus;
import tosiltosil.backend.module.stopwatch.domain.event.StopwatchPausedEvent;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
class GoalEventHandlerTest {

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private GoalEventHandler goalEventHandler;

    @Test
    void 스톱워치_정지_이벤트로_목표에_진행_시간_추가() {
        // given
        UUID memberId = UUID.randomUUID();
        LocalDateTime startTime = LocalDateTime.of(2024, 1, 1, 10, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 1, 1, 11, 30, 0);
        Duration currentDuration = Duration.ofHours(30);
        Duration totalTime = Duration.ofHours(3);
        Duration expectedAddedDuration = Duration.between(startTime, endTime); // 1시간 30분

        Goal goal = createGoal(memberId, totalTime, currentDuration);
        StopwatchPausedEvent event = new StopwatchPausedEvent(
                memberId, 1L, "PAUSED", startTime, endTime, currentDuration.plus(expectedAddedDuration)
        );

        given(goalRepository.findById(any())).willReturn(Optional.of(goal));

        // when
        goalEventHandler.addDuration(event);

        // then
        Duration expectedTotalDuration = currentDuration.plus(expectedAddedDuration);
        assertThat(goal.getDuration()).isEqualTo(expectedTotalDuration);
    }

    @Test
    void 추가된_진행_시간으로_목표가_완료되면_상태를_완료로_변경() {
        // given
        UUID memberId = UUID.randomUUID();
        LocalDateTime startTime = LocalDateTime.of(2024, 1, 1, 10, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 1, 1, 12, 0, 0);
        Duration currentDuration = Duration.ofHours(1);
        Duration totalTime = Duration.ofHours(3);
        Duration expectedAddedDuration = Duration.between(startTime, endTime);

        // 기존 진행시간 1시간 + 추가 2시간 = 총 3시간으로 목표시간(3시간) 달성
        Goal goal = createGoal(memberId, totalTime, currentDuration);
        StopwatchPausedEvent event = new StopwatchPausedEvent(
                memberId, 1L, "PAUSED", startTime, endTime, currentDuration.plus(expectedAddedDuration)
        );

        given(goalRepository.findById(any())).willReturn(Optional.of(goal));

        // when
        goalEventHandler.addDuration(event);

        // then
        assertThat(goal.getStatus()).isEqualTo(GoalStatus.COMPLETED);
    }

    private Goal createGoal(UUID memberId, Duration totalTime, Duration currentDuration) {
        return Goal.builder()
                .memberId(memberId)
                .categoryId(1L)
                .title("Test Goal")
                .totalTime(totalTime)
                .status(GoalStatus.RUNNING)
                .duration(currentDuration)
                .orderIndex(BigDecimal.valueOf(100000.0))
                .iconId(1L)
                .date(LocalDate.now())
                .build();
    }
}