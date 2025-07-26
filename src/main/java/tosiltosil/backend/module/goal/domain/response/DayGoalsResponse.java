package tosiltosil.backend.module.goal.domain.response;

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
}