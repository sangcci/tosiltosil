package tosiltosil.backend.common.auth.domain.response;

import io.jsonwebtoken.Claims;

import java.util.UUID;

public record RefreshTokenInfo(
        UUID memberId,
        String token
) {
    public static RefreshTokenInfo from(String token, Claims claims) {
        UUID memberId = UUID.fromString(claims.getSubject());
        return new RefreshTokenInfo(memberId, token);
    }
}
