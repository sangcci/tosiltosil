package tosiltosil.backend.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import tosiltosil.backend.common.properties.JwtProperties;

@Component
@RequiredArgsConstructor
public class CookieUtil {

    private final JwtProperties jwtProperties;

    public HttpHeaders generateAccessAndRefreshTokenCookies(String accessToken, String refreshToken) {
        String accessCookieName = jwtProperties.cookie().name().access();
        long accessTtl = jwtProperties.expiration().access();

        String refreshCookieName = jwtProperties.cookie().name().refresh();
        long refreshTtl = jwtProperties.expiration().refresh();

        ResponseCookie accessTokenCookie = generateCookie(accessCookieName, accessToken, accessTtl);
        ResponseCookie refreshTokenCookie = generateCookie(refreshCookieName, refreshToken, refreshTtl);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return headers;
    }

    public HttpHeaders generateTemporaryTokenCookies(String temporaryToken) {
        String temporaryCookieName = jwtProperties.cookie().name().temporary();
        long temporaryTtl = jwtProperties.expiration().temporary();
        ResponseCookie temporaryTokenCookie = generateCookie(temporaryCookieName, temporaryToken, temporaryTtl);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, temporaryTokenCookie.toString());
        return headers;
    }

    private ResponseCookie generateCookie(String name, String value, long maxAge) {
        String sameSite = jwtProperties.cookie().sameSite();
        boolean secure = jwtProperties.cookie().secure();

        return ResponseCookie.from(name, value)
                        .httpOnly(true)
                        .secure(secure)
                        .sameSite(sameSite)
                        .path("/")
                        .maxAge(maxAge)
                        .build();
    }
}
