package tosiltosil.backend.common.auth.domain.response;

import io.jsonwebtoken.Claims;

import java.util.UUID;

public record AccessTokenInfo(
        UUID memberId,
        String token
) {
    public static AccessTokenInfo from(String token, Claims claims) {
        UUID memberId = UUID.fromString(claims.getSubject());
        return new AccessTokenInfo(memberId, token);
    }
}
