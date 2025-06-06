package tosiltosil.backend.module;

import jakarta.validation.constraints.NotBlank;
import tosiltosil.backend.common.domain.deserializer.DeserializedEnum;
import tosiltosil.backend.common.domain.validator.Enum;

public record HealthCheckRequest(
        @NotBlank
        String message,
        @DeserializedEnum @Enum
        HealthType healthType
) {

}
