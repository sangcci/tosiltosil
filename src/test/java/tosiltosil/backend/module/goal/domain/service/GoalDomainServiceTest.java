package tosiltosil.backend.module.goal.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tosiltosil.backend.common.domain.exception.BadRequestException;
import tosiltosil.backend.common.domain.holder.TestTimeHolder;
import tosiltosil.backend.common.domain.holder.TimeHolder;
import tosiltosil.backend.module.goal.domain.Goal;
import tosiltosil.backend.module.goal.domain.GoalRepository;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
class GoalDomainServiceTest {

    @Mock
    private GoalRepository goalRepository;

    private GoalDomainService goalDomainService;

    @BeforeEach
    void setUp() {
        TimeHolder testTimeHolder = new TestTimeHolder(LocalDate.of(2025, 7, 8));
        goalDomainService = new GoalDomainService(goalRepository, testTimeHolder);
    }

    @Test
    void 목표_날짜_검증_시_오늘_날짜로_생성하면_성공() {
        // given
        LocalDate today = LocalDate.of(2025, 7, 8);

        // when & then
        assertThatCode(() -> goalDomainService.validateGoalDate(today))
                .doesNotThrowAnyException();
    }

    @Test
    void 목표_날짜_검증_시_미래_날짜로_생성하면_성공() {
        // given
        LocalDate today = LocalDate.of(2025, 7, 8);
        
        LocalDate futureDate = today.plusDays(1);

        // when & then
        assertThatCode(() -> goalDomainService.validateGoalDate(futureDate))
                .doesNotThrowAnyException();
    }

    @Test
    void 목표_날짜_검증_시_과거_날짜로_생성하면_실패() {
        // given
        LocalDate today = LocalDate.of(2025, 7, 8);
        
        LocalDate pastDate = today.minusDays(1);

        // when & then
        assertThatThrownBy(() -> goalDomainService.validateGoalDate(pastDate))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("날짜는 오늘 이후여야 합니다.");
    }

    @Test
    void 목표_달성률_계산_시_오늘의_목표가_없으면_0_반환() {
        // given
        UUID memberId = UUID.randomUUID();
        when(goalRepository.findTodayGoals(memberId)).thenReturn(Collections.emptyList());

        // when
        BigDecimal percentage = goalDomainService.calculateGoalAchievedPercentage(memberId);

        // then
        assertThat(percentage).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void 목표_달성률_계산_시_총_시간이_0이면_0_반환() {
        // given
        UUID memberId = UUID.randomUUID();
        Goal goal = createGoal(memberId, Duration.ZERO, Duration.ofHours(1));
        when(goalRepository.findTodayGoals(memberId)).thenReturn(List.of(goal));

        // when
        BigDecimal percentage = goalDomainService.calculateGoalAchievedPercentage(memberId);

        // then
        assertThat(percentage).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void 목표_달성률_계산_시_소수점_내림하여_반환() {
        // given
        UUID memberId = UUID.randomUUID();
        Goal goal = createGoal(memberId, Duration.ofHours(3), Duration.ofHours(1));
        when(goalRepository.findTodayGoals(memberId)).thenReturn(List.of(goal));

        // when
        BigDecimal percentage = goalDomainService.calculateGoalAchievedPercentage(memberId);

        // then
        assertThat(percentage).isEqualTo(new BigDecimal("33"));
    }

    @Test
    void 목표_달성률_계산_시_목표_시간을_초과했으면_100_반환() {
        // given
        UUID memberId = UUID.randomUUID();
        Goal goal = createGoal(memberId, Duration.ofHours(1), Duration.ofHours(2));
        when(goalRepository.findTodayGoals(memberId)).thenReturn(List.of(goal));

        // when
        BigDecimal percentage = goalDomainService.calculateGoalAchievedPercentage(memberId);

        // then
        assertThat(percentage).isEqualTo(new BigDecimal("100"));
    }

    private Goal createGoal(final UUID memberId, final Duration totalTime, final Duration duration) {
        return Goal.builder()
                .memberId(memberId)
                .categoryId(1L)
                .title("테스트 목표")
                .totalTime(totalTime)
                .duration(duration)
                .orderIndex(BigDecimal.ONE)
                .iconId(1L)
                .date(LocalDate.of(2025, 7, 8))
                .build();
    }
}