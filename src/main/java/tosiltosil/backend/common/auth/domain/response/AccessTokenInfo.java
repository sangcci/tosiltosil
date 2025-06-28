package tosiltosil.backend.common.auth.domain.response;

import io.jsonwebtoken.Claims;
import tosiltosil.backend.common.auth.domain.TokenType;

import java.util.UUID;

public record AccessTokenInfo(
        UUID memberId,
        TokenType tokenType
) {
    public static AccessTokenInfo from(Claims claims) {
        return new AccessTokenInfo(
                UUID.fromString(claims.getSubject()),
                TokenType.valueOf(claims.get("type", String.class))
        );
    }
}
