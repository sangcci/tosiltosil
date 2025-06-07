package tosiltosil.backend.module;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tosiltosil.backend.common.web.response.Response;

@RestController
@RequestMapping("/api/v1/health")
public class HealthCheckController implements HealthCheckApiSpecification {

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Response<String> healthCheck(
            @Valid @RequestBody final HealthCheckRequest request
    ) {
        HealthType healthType = HealthType.valueOf(request.healthType());
        return Response.ok("OK", healthType.name());
    }
}
