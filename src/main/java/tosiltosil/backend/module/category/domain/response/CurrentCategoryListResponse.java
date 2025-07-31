package tosiltosil.backend.module.category.domain.response;

import tosiltosil.backend.module.category.domain.Category;
import tosiltosil.backend.module.category.domain.value.CategoryColor;

public record CurrentCategoryListResponse(
        Long categoryId,
        String title,
        CategoryColor color
) {

    public static CurrentCategoryListResponse of(final Category category) {
        return new CurrentCategoryListResponse(category.getId(), category.getTitle(), category.getColor());
    }
}
