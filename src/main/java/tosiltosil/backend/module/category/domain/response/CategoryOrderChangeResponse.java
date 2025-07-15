package tosiltosil.backend.module.category.domain.response;

import java.math.BigDecimal;

public record CategoryOrderChangeResponse(
        BigDecimal orderIndex
) {

    public static CategoryOrderChangeResponse of(final BigDecimal orderIndex) {
        return new CategoryOrderChangeResponse(orderIndex);
    }
}
