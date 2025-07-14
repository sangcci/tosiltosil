package tosiltosil.backend.module.category.domain.response;

public record CategoryOrderChangeResponse(
        String orderKey
) {

    public static CategoryOrderChangeResponse of(final String orderKey) {
        return new CategoryOrderChangeResponse(orderKey);
    }
}
