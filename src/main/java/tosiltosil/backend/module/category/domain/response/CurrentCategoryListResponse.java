package tosiltosil.backend.module.category.domain.response;

import tosiltosil.backend.module.category.domain.Category;

public record CurrentCategoryListResponse(
        Long categoryId,
        String title,
        String color
) {

    public static CurrentCategoryListResponse of(final Category category) {
        return new CurrentCategoryListResponse(category.getId(), category.getTitle(), category.getColor());
    }
}
