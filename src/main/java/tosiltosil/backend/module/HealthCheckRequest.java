package tosiltosil.backend.module;

import jakarta.validation.constraints.NotBlank;
import tosiltosil.backend.common.domain.deserializer.ValidEnum;

public record HealthCheckRequest(
        @NotBlank
        String message,
        @ValidEnum
        HealthType healthType
) {

}
