package tosiltosil.backend.common.auth.domain.response;

import io.jsonwebtoken.Claims;
import tosiltosil.backend.common.auth.domain.value.TokenType;

import java.util.UUID;

public record AccessTokenInfo(
        UUID memberId,
        String token,
        TokenType tokenType
) {
    public static AccessTokenInfo of(UUID memberId, String token) {
        return new AccessTokenInfo(memberId, token, TokenType.ACCESS);
    }

    public static AccessTokenInfo from(String token, Claims claims) {
        return new AccessTokenInfo(
                UUID.fromString(claims.getSubject()),
                token,
                TokenType.valueOf(claims.get("type", String.class))
        );
    }
}
