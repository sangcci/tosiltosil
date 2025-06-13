package tosiltosil.backend.module.category.domain.response;

public record CategoryResponse(
        Long categoryId
) {

    public static CategoryResponse of(final Long categoryId) {
        return new CategoryResponse(categoryId);
    }
}
