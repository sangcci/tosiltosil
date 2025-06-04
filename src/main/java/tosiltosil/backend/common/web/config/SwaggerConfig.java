package tosiltosil.backend.common.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private final String serverUrl;

    public SwaggerConfig(@Value("${springdoc.server.url}") final String serverUrl) {
        this.serverUrl = serverUrl;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        Info info = new Info()
                .title("tosiltosil API")
                .description("토실토실 API 입니다.")
                .version("v0.0.1");

        Server httpsServer = new Server()
                .url(serverUrl);

        return new OpenAPI()
                .info(info)
                .servers(List.of(httpsServer));
    }
}
