package tosiltosil.backend.common.monitoring;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;

@SuppressWarnings("unused")
public interface HealthCheckApiSpecification {

    @Tag(name = "GET", description = "HEALTH CHECK")
    @Operation(summary = "health check", description = "💡서버의 on/off 상태를 확인합니다.")
    @ApiResponses(value = {@ApiResponse(
                    responseCode = "200", description = "SUCCESS",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            examples = {@ExampleObject(value = "OK")}
                    )
            ),
    })
    String healthCheck();
}
