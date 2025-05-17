package tosiltosil.backend.module;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tosiltosil.backend.common.web.response.Response;

@RestController
@RequestMapping("/api/v1/health")
public class HealthCheckController implements HealthCheckApiSpecification {

    @GetMapping
    public Response<String> healthCheck() {
        return Response.ok("healthCheck");
    }
}
