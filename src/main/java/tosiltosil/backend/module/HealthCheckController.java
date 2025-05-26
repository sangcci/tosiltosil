package tosiltosil.backend.module;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tosiltosil.backend.common.web.response.Response;

@RestController
@RequestMapping("/api/v1/health")
public class HealthCheckController implements HealthCheckApiSpecification {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Response<String> healthCheck() {
        return Response.ok("OK", "healthCheck");
    }
}
