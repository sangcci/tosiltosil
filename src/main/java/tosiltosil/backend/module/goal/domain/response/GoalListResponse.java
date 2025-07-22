package tosiltosil.backend.module.goal.domain.response;

import tosiltosil.backend.module.goal.domain.Goal;
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

    public static GoalListResponse of(final Goal goal) {
        return new GoalListResponse(
                goal.getId(),
                goal.getCategoryId(),
                goal.getIconId(),
                goal.getTitle(),
                goal.getStatus(),
                goal.getTotalTime().toString(),
                goal.getDuration().toString(),
                goal.getOrderIndex()
        );
    }

}