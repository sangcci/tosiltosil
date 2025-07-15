package tosiltosil.backend.module.goal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import tosiltosil.backend.common.domain.exception.ConflictException;
import tosiltosil.backend.common.domain.exception.ForbiddenException;
import tosiltosil.backend.module.goal.domain.value.GoalStatus;

@SuppressWarnings("NonAsciiCharacters")
class GoalTest {

    @Test
    void 자신의_목표가_아니면_예외_발생() {
        // given
        UUID memberId = UUID.randomUUID();
        UUID differentMemberId = UUID.randomUUID();
        Goal goal = createGoalWithStatus(memberId, GoalStatus.BEFORE_STARTING);

        // when & then
        assertThatThrownBy(() -> goal.validateIsMine(differentMemberId))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("해당 목표에 접근할 권한이 없습니다.");
    }

    @Test
    void BEFORE_STARTING_상태에서_시작_상태로_변경한다() {
        // given
        UUID memberId = UUID.randomUUID();
        Goal goal = createGoalWithStatus(memberId, GoalStatus.BEFORE_STARTING);
        
        // when
        goal.changeStatusToStarted();
        
        // then
        assertThat(goal.getStatus()).isEqualTo(GoalStatus.RUNNING);
    }

    @Test
    void PAUSED_상태에서_시작_상태로_변경한다() {
        // given
        UUID memberId = UUID.randomUUID();
        Goal goal = createGoalWithStatus(memberId, GoalStatus.PAUSED);
        
        // when
        goal.changeStatusToStarted();
        
        // then
        assertThat(goal.getStatus()).isEqualTo(GoalStatus.RUNNING);
    }

    @Test
    void RUNNING_상태에서_시작_상태로_변경하면_예외가_발생한다() {
        // given
        UUID memberId = UUID.randomUUID();
        Goal goal = createGoalWithStatus(memberId, GoalStatus.RUNNING);
        
        // when & then
        assertThatThrownBy(goal::changeStatusToStarted)
                .isInstanceOf(ConflictException.class)
                .hasMessage("스톱워치가 이미 실행되거나 기간이 지난 상태입니다.");
    }

    @Test
    void COMPLETED_상태에서_시작_상태로_변경하면_예외가_발생한다() {
        // given
        UUID memberId = UUID.randomUUID();
        Goal goal = createGoalWithStatus(memberId, GoalStatus.COMPLETED);
        
        // when & then
        assertThatThrownBy(goal::changeStatusToStarted)
                .isInstanceOf(ConflictException.class)
                .hasMessage("스톱워치가 이미 실행되거나 기간이 지난 상태입니다.");
    }

    @Test
    void RUNNING_상태에서_일시정지_상태로_변경한다() {
        // given
        UUID memberId = UUID.randomUUID();
        Goal goal = createGoalWithStatus(memberId, GoalStatus.RUNNING);
        
        // when
        goal.changeStatusToPaused();
        
        // then
        assertThat(goal.getStatus()).isEqualTo(GoalStatus.PAUSED);
    }

    @Test
    void BEFORE_STARTING_상태에서_일시정지_상태로_변경하면_예외가_발생한다() {
        // given
        UUID memberId = UUID.randomUUID();
        Goal goal = createGoalWithStatus(memberId, GoalStatus.BEFORE_STARTING);
        
        // when & then
        assertThatThrownBy(goal::changeStatusToPaused)
                .isInstanceOf(ConflictException.class)
                .hasMessage("스톱워치가 이미 정지되었습니다.");
    }

    @Test
    void PAUSED_상태에서_일시정지_상태로_변경하면_예외가_발생한다() {
        // given
        UUID memberId = UUID.randomUUID();
        Goal goal = createGoalWithStatus(memberId, GoalStatus.PAUSED);
        
        // when & then
        assertThatThrownBy(goal::changeStatusToPaused)
                .isInstanceOf(ConflictException.class)
                .hasMessage("스톱워치가 이미 정지되었습니다.");
    }

    private Goal createGoalWithStatus(UUID memberId, GoalStatus status) {
        return Goal.builder()
                .memberId(memberId)
                .categoryId(1L)
                .title("Test Goal")
                .totalTime(Duration.ofHours(2))
                .status(status)
                .duration(Duration.ZERO)
                .orderIndex(BigDecimal.valueOf(100000.0))
                .iconId(1L)
                .date(LocalDate.now())
                .build();
    }
}