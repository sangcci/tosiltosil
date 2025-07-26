package tosiltosil.backend.module.goal.domain.response;

import tosiltosil.backend.module.goal.domain.value.GoalStatus;

import java.math.BigDecimal;

public record GoalListResponse(
        Long goalId,
        Long categoryId,
        Long iconId,
        String title,
        GoalStatus status,
        String totalTime,
        String duration,
        BigDecimal orderIndex
) {

}