package tosiltosil.backend.module;

import tosiltosil.backend.common.web.response.Response;

@SuppressWarnings("unused")
public interface HealthCheckApiSpecification {

    Response<String> healthCheck(HealthCheckRequest request);
}
