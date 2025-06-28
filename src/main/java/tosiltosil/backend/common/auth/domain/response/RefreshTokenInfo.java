package tosiltosil.backend.common.auth.domain.response;

import io.jsonwebtoken.Claims;
import tosiltosil.backend.common.auth.domain.value.TokenType;

import java.util.UUID;

public record RefreshTokenInfo(
        UUID memberId,
        String token,
        TokenType tokenType
) {
    public static RefreshTokenInfo of(UUID memberId, String token) {
        return new RefreshTokenInfo(memberId, token, TokenType.REFRESH);
    }

    public static RefreshTokenInfo from(String token, Claims claims) {
        return new RefreshTokenInfo(
                UUID.fromString(claims.getSubject()),
                token,
                TokenType.valueOf(claims.get("type", String.class))
        );
    }
}
