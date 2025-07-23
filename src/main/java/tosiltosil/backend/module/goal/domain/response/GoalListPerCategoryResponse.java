package tosiltosil.backend.module.goal.domain.response;

import tosiltosil.backend.module.category.domain.value.CategoryColor;

import java.math.BigDecimal;
import java.util.List;

public record GoalListPerCategoryResponse(
        Long categoryId,
        String categoryTitle,
        CategoryColor categoryColor,
        BigDecimal categoryOrderIndex,
        List<GoalListResponse> goals
) {

}