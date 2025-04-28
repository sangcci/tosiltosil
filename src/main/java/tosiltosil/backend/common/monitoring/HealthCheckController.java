package tosiltosil.backend.common.monitoring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthCheckController implements HealthCheckApiSpecification {

    @GetMapping
    public String healthCheck() {
        return "OK";
    }
}
