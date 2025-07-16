package tosiltosil.backend.module.goal.domain.request;

import java.math.BigDecimal;

public record GoalOrderChangeRequest(
        Long goalId,
        BigDecimal prevOrderIndex,
        BigDecimal nextOrderIndex
) {

}
