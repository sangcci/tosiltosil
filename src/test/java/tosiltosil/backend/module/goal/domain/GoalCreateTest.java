package tosiltosil.backend.module.goal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import tosiltosil.backend.common.domain.exception.BadRequestException;

@SuppressWarnings("NonAsciiCharacters")
class GoalCreateTest {

    @Test
    void 시간이_1분_미만이면_예외_발생() {
        // given
        UUID memberId = UUID.randomUUID();
        Duration invalidTime = Duration.ofSeconds(30);

        // when & then
        assertThatThrownBy(() -> Goal.of(memberId, 1L, "테스트", invalidTime, 1, 1L, LocalDate.now()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("시간은 0시 1분 이상 23시 59분 이하가 되어야 합니다");
    }

    @Test
    void 시간이_24시간_이상이면_예외_발생() {
        // given
        UUID memberId = UUID.randomUUID();
        Duration invalidTime = Duration.ofHours(24);

        // when & then
        assertThatThrownBy(() -> Goal.of(memberId, 1L, "테스트", invalidTime, 1, 1L, LocalDate.now()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("시간은 0시 1분 이상 23시 59분 이하가 되어야 합니다");
    }

    @Test
    void 시간이_0이면_예외_발생() {
        // given
        UUID memberId = UUID.randomUUID();
        Duration invalidTime = Duration.ZERO;

        // when & then
        assertThatThrownBy(() -> Goal.of(memberId, 1L, "테스트", invalidTime, 1, 1L, LocalDate.now()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("시간은 0시 1분 이상 23시 59분 이하가 되어야 합니다");
    }

    @Test
    void 시간이_음수면_예외_발생() {
        // given
        UUID memberId = UUID.randomUUID();
        Duration invalidTime = Duration.ofMinutes(-1);

        // when & then
        assertThatThrownBy(() -> Goal.of(memberId, 1L, "테스트", invalidTime, 1, 1L, LocalDate.now()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("시간은 0시 1분 이상 23시 59분 이하가 되어야 합니다");
    }

    @Test
    void 시간_경계값_테스트_1분() {
        // given
        UUID memberId = UUID.randomUUID();
        Duration oneMinute = Duration.ofMinutes(1);

        // when
        Goal goal = Goal.of(memberId, 1L, "테스트", oneMinute, 1, 1L, LocalDate.now());

        // then
        assertThat(goal.getTotalTime()).isEqualTo(oneMinute);
    }

    @Test
    void 시간_경계값_테스트_23시간59분() {
        // given
        UUID memberId = UUID.randomUUID();
        Duration maxTime = Duration.ofHours(23).plusMinutes(59);

        // when
        Goal goal = Goal.of(memberId, 1L, "테스트", maxTime, 1, 1L, LocalDate.now());

        // then
        assertThat(goal.getTotalTime()).isEqualTo(maxTime);
    }
}