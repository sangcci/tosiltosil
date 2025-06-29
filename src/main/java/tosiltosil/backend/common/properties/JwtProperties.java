package tosiltosil.backend.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        Secret secret,
        Expiration expiration,
        Cookie cookie
) {
    public record Secret(
            String temporary,
            String access,
            String refresh
    ) {}

    public record Expiration(
            Long temporary,
            Long access,
            Long refresh
    ) {}

    public record Cookie(
            CookieName name,
            boolean secure,
            String sameSite
    ) {}

    public record CookieName(
            String temporary,
            String access,
            String refresh
    ) {}
}
