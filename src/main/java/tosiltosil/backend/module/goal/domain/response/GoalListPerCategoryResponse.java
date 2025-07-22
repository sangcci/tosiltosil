package tosiltosil.backend.module.goal.domain.response;

import tosiltosil.backend.module.category.domain.Category;

import java.math.BigDecimal;
import java.util.List;

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