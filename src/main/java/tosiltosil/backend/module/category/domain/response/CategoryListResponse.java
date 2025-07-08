package tosiltosil.backend.module.category.domain.response;

import tosiltosil.backend.module.category.domain.Category;

public record CategoryListResponse(
        Long categoryId,
        String title,
        String color
) {

    public static CategoryListResponse of(final Category category) {
        return new CategoryListResponse(category.getId(), category.getTitle(), category.getColor());
    }
}
