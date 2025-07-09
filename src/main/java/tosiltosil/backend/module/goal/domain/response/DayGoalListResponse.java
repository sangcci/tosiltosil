package tosiltosil.backend.module.goal.domain.response;

import java.util.List;

public record DayGoalListResponse(
        Long categoryId,
        String categoryTitle,
        String categoryColor,
        List<GoalListResponse> goals
) {

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

    }
}