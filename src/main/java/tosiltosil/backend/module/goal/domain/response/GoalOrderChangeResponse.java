package tosiltosil.backend.module.goal.domain.response;

import java.math.BigDecimal;

public record GoalOrderChangeResponse(
        BigDecimal orderIndex
) {

    public static GoalOrderChangeResponse of(final BigDecimal orderIndex) {
        return new GoalOrderChangeResponse(orderIndex);
    }
}
