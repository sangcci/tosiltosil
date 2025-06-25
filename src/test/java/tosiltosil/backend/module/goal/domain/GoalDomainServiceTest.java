package tosiltosil.backend.module.goal.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tosiltosil.backend.module.goal.domain.service.GoalDomainService;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
class GoalDomainServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private GoalDomainService goalDomainService;

    @Test
    void 사용자의_하루_총_누적_시간이_24시간_이후라면_예외를_던진다() {
        // given
        UUID memberId = UUID.randomUUID();
        Goal goal1 = createGoal(memberId, Duration.ofHours(12));
        Goal goal2 = createGoal(memberId, Duration.ofHours(13));
        List<Goal> todayGoals = List.of(goal1, goal2);
        
        when(goalRepository.findTodayGoalsByMemberId(memberId)).thenReturn(todayGoals);

        // When & Then
        assertThatThrownBy(() -> goalDomainService.validateCreation(memberId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("일일 목표 총 시간인 24시간을 초과하여 목표를 생성할 수 없습니다.");
    }

    @Test
    void 사용자의_하루_총_누적_시간이_24시간_이내라면_성공한다() {
        // Given
        UUID memberId = UUID.randomUUID();
        Goal goal1 = createGoal(memberId, Duration.ofHours(10));
        Goal goal2 = createGoal(memberId, Duration.ofHours(8));
        List<Goal> todayGoals = List.of(goal1, goal2);
        
        when(goalRepository.findTodayGoalsByMemberId(memberId)).thenReturn(todayGoals);

        // When & Then
        assertThatCode(() -> goalDomainService.validateCreation(memberId))
                .doesNotThrowAnyException();
    }

    @Test
    void 사용자의_하루_총_누적_시간이_정확히_24시간이라면_예외를_던진다() {
        // Given
        UUID memberId = UUID.randomUUID();
        Goal goal1 = createGoal(memberId, Duration.ofHours(12));
        Goal goal2 = createGoal(memberId, Duration.ofHours(12));
        List<Goal> todayGoals = List.of(goal1, goal2);
        
        when(goalRepository.findTodayGoalsByMemberId(memberId)).thenReturn(todayGoals);

        // When & Then
        assertThatThrownBy(() -> goalDomainService.validateCreation(memberId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("일일 목표 총 시간인 24시간을 초과하여 목표를 생성할 수 없습니다.");
    }

    private Goal createGoal(final UUID memberId, final Duration totalTime) {
        return Goal.of(
                memberId,
                1L,
                "Test Goal",
                totalTime,
                1,
                1L,
                LocalDate.now()
        );
    }
}
