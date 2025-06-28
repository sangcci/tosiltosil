package tosiltosil.backend.common.auth.domain.response;

import io.jsonwebtoken.Claims;
import tosiltosil.backend.common.auth.domain.TokenType;

public record TemporaryTokenInfo(
        String key,
        TokenType tokenType
) {
    public static TemporaryTokenInfo from(Claims claims) {
        return new TemporaryTokenInfo(
                claims.get("key", String.class),
                TokenType.valueOf(claims.get("type", String.class))
        );
    }
}
