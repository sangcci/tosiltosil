package tosiltosil.backend.common.auth.domain.response;

import io.jsonwebtoken.Claims;
import tosiltosil.backend.common.auth.domain.TokenType;

import java.util.UUID;

public record RefreshTokenInfo(
        UUID memberId,
        TokenType tokenType
) {
    public static RefreshTokenInfo from(Claims claims) {
        return new RefreshTokenInfo(
                UUID.fromString(claims.getSubject()),
                TokenType.valueOf(claims.get("type", String.class))
        );
    }
}
