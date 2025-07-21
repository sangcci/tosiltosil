package tosiltosil.backend.module.goal.domain.response;

import tosiltosil.backend.module.category.domain.Category;
import tosiltosil.backend.module.goal.domain.Goal;
import tosiltosil.backend.module.goal.domain.value.GoalStatus;

import java.math.BigDecimal;
import java.util.List;

public record DayGoalsResponse(
        BigDecimal achievedPercentage,
        List<GoalListPerCategoryResponse> categories
) {

    public static DayGoalsResponse of(
            final BigDecimal achievedPercentage,
            final List<GoalListPerCategoryResponse> categories
    ) {
        return new DayGoalsResponse(achievedPercentage, categories);
    }

    public record GoalListPerCategoryResponse(
            Long categoryId,
            String categoryTitle,
            String categoryColor,
            BigDecimal categoryOrderIndex,
            List<GoalListResponse> goals
    ) {

        public static GoalListPerCategoryResponse of(
                final Category category,
                final List<GoalListResponse> goals
        ) {
            return new GoalListPerCategoryResponse(
                    category.getId(),
                    category.getTitle(),
                    category.getColor(),
                    category.getOrderIndex(),
                    goals
            );
        }
    }

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
}