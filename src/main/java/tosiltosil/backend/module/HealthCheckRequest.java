package tosiltosil.backend.module;

import jakarta.validation.constraints.NotBlank;
import tosiltosil.backend.common.domain.validator.IsEnum;

public record HealthCheckRequest(
        @NotBlank
        String message,
        @IsEnum(enumClass = HealthType.class)
        String healthType
) {

}
