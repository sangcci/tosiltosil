package tosiltosil.backend.module;

import jakarta.validation.constraints.NotBlank;
import tosiltosil.backend.common.domain.validator.Enum;

public record HealthCheckRequest(
        @NotBlank
        String message,
        @Enum(enumClass = HealthType.class)
        String healthType
) {

}
