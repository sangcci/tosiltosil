package tosiltosil.backend.module;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import tosiltosil.backend.common.web.response.Response;

@SuppressWarnings("unused")
public interface HealthCheckApiSpecification {

    @Tag(name = "POST", description = "HEALTH CHECK")
    @Operation(summary = "health check", description = "💡서버의 on/off 상태를 확인합니다.")
    Response<String> healthCheck(HealthCheckRequest request);
}
