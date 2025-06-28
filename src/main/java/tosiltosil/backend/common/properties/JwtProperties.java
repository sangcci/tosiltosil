package tosiltosil.backend.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String temporaryTokenSecret,
        String accessTokenSecret,
        String refreshTokenSecret,
        Expiration expiration
) {
    public record Expiration(
            Long temporary,
            Long access,
            Long refresh
    ) {}
}
