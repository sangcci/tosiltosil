package tosiltosil.backend.module.category.domain.request;

import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public record CategoryOrderChangeRequest(
        @DecimalMin(value = "0.0", inclusive = false, message = "순서는 음수일 수 없습니다.")
        BigDecimal prevOrderIndex,
        @DecimalMin(value = "0.0", inclusive = false, message = "순서는 음수일 수 없습니다.")
        BigDecimal nextOrderIndex
) {

}
