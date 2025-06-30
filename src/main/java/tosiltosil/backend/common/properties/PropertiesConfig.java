package tosiltosil.backend.common.properties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({
        JwtProperties.class,
        RedisProperties.class
})
@Configuration
public class PropertiesConfig {
}
