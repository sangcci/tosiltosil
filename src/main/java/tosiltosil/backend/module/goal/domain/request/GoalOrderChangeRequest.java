package tosiltosil.backend.module.goal.domain.request;

import java.math.BigDecimal;

public record GoalOrderChangeRequest(
        Long goalId,
        Integer targetIndex,
        BigDecimal prevOrderIndex,
        BigDecimal nextOrderIndex
) {

}
