package tosiltosil.backend.module.category.domain.response;

import java.math.BigDecimal;
import tosiltosil.backend.module.category.domain.Category;
import tosiltosil.backend.module.category.domain.value.CategoryColor;

public record CurrentCategoryListResponse(
        Long categoryId,
        String title,
        CategoryColor color,
        BigDecimal orderIndex
) {

    public static CurrentCategoryListResponse of(final Category category) {
        return new CurrentCategoryListResponse(category.getId(), category.getTitle(), category.getColor(), category.getOrderIndex());
    }
}
