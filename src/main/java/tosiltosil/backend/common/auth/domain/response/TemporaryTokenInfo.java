package tosiltosil.backend.common.auth.domain.response;

import io.jsonwebtoken.Claims;

public record TemporaryTokenInfo(
        String email,
        String token
) {
    public static TemporaryTokenInfo from(String token, Claims claims) {
        String email = claims.getSubject();
        return new TemporaryTokenInfo(email, token);
    }
}
