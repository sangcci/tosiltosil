package tosiltosil.backend.module.category.domain.response;

import java.math.BigDecimal;
import tosiltosil.backend.module.category.domain.Category;

public record CurrentCategoryListResponse(
        Long categoryId,
        String title,
        String color,
        BigDecimal orderIndex
) {

    public static CurrentCategoryListResponse of(final Category category) {
        return new CurrentCategoryListResponse(category.getId(), category.getTitle(), category.getColor(), category.getOrderIndex());
    }
}
