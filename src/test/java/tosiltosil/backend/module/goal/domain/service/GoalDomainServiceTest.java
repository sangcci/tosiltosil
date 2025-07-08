package tosiltosil.backend.module.goal.domain.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tosiltosil.backend.common.domain.exception.BadRequestException;
import tosiltosil.backend.common.domain.holder.TestTimeHolder;

@SuppressWarnings("NonAsciiCharacters")
class GoalDomainServiceTest {

    private GoalDomainService goalDomainService;

    @BeforeEach
    void setUp() {
        TestTimeHolder testTimeHolder = new TestTimeHolder(LocalDate.of(2025, 7, 8));
        goalDomainService = new GoalDomainService(testTimeHolder);
    }

    @Test
    void 목표_날짜_검증_시_오늘_날짜로_생성하면_성공한다() {
        // given
        LocalDate today = LocalDate.of(2025, 7, 8);

        // when & then
        assertThatCode(() -> goalDomainService.validateGoalDate(today))
                .doesNotThrowAnyException();
    }

    @Test
    void 목표_날짜_검증_시_미래_날짜로_생성하면_성공한다() {
        // given
        LocalDate today = LocalDate.of(2025, 7, 8);
        
        LocalDate futureDate = today.plusDays(1);

        // when & then
        assertThatCode(() -> goalDomainService.validateGoalDate(futureDate))
                .doesNotThrowAnyException();
    }

    @Test
    void 목표_날짜_검증_시_과거_날짜로_생성하면_실패한다() {
        // given
        LocalDate today = LocalDate.of(2025, 7, 8);
        
        LocalDate pastDate = today.minusDays(1);

        // when & then
        assertThatThrownBy(() -> goalDomainService.validateGoalDate(pastDate))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("날짜는 오늘 이후여야 합니다.");
    }
}