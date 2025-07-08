package tosiltosil.backend.module.goal.domain.response;

import tosiltosil.backend.module.goal.domain.Goal;

public record GoalListResponse(
        Long goalId,
        Long categoryId,
        //Long order,
        Long iconId,
        String title,
        String status,
        String totalTime,
        String duration
) {

    public static GoalListResponse of(final Goal goal) {
        return new GoalListResponse(
                goal.getId(),
                goal.getCategoryId(),
                goal.getIconId(),
                goal.getTitle(),
                goal.getStatus().name(),
                goal.getTotalTime().toString(),
                goal.getDuration().toString()
        );
    }
}
