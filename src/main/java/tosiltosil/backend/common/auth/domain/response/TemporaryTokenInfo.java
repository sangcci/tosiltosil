package tosiltosil.backend.common.auth.domain.response;

import io.jsonwebtoken.Claims;
import tosiltosil.backend.common.auth.domain.value.TokenType;

import java.util.UUID;

public record TemporaryTokenInfo(
        UUID key,
        TokenType tokenType
) {
    public static TemporaryTokenInfo from(Claims claims) {
        return new TemporaryTokenInfo(
                UUID.fromString(claims.get("cacheKey", String.class)),
                TokenType.valueOf(claims.get("type", String.class))
        );
    }
}
