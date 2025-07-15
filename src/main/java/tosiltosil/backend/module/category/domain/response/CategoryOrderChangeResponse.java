package tosiltosil.backend.module.category.domain.response;

public record CategoryOrderChangeResponse(
        Double orderIndex
) {

    public static CategoryOrderChangeResponse of(final Double orderIndex) {
        return new CategoryOrderChangeResponse(orderIndex);
    }
}
