package tosiltosil.backend.module.category.domain.request;

import java.math.BigDecimal;

public record CategoryOrderChangeRequest(
        Long categoryId,
        BigDecimal prevOrderIndex,
        BigDecimal nextOrderIndex
) {

}
