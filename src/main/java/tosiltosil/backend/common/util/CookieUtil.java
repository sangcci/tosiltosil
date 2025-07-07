package tosiltosil.backend.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CookieUtil {

    @Value("${jwt.cookie.name.access}")
    private String accessCookieName;

    @Value("${jwt.cookie.name.refresh}")
    private String refreshCookieName;

    @Value("${jwt.cookie.name.temporary}")
    private String temporaryCookieName;

    @Value("${jwt.expiration.access}")
    private long accessTtl;

    @Value("${jwt.expiration.refresh}")
    private long refreshTtl;

    @Value("${jwt.expiration.temporary}")
    private long temporaryTtl;

    @Value("${jwt.cookie.secure}")
    private boolean secure;

    @Value("${jwt.cookie.same-site}")
    private String sameSite;

    public HttpHeaders generateAccessAndRefreshTokenCookies(String accessToken, String refreshToken) {
        ResponseCookie accessTokenCookie = generateCookie(accessCookieName, accessToken, accessTtl);
        ResponseCookie refreshTokenCookie = generateCookie(refreshCookieName, refreshToken, refreshTtl);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return headers;
    }

    public HttpHeaders generateTemporaryTokenCookies(String temporaryToken) {
        ResponseCookie temporaryTokenCookie = generateCookie(temporaryCookieName, temporaryToken, temporaryTtl);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, temporaryTokenCookie.toString());
        return headers;
    }

    public HttpHeaders generateClientIdCookie(UUID clientId) {
        ResponseCookie clientIdCookie = generateCookie("client-id", clientId.toString());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, clientIdCookie.toString());
        return headers;
    }

    public HttpHeaders deleteAccessAndRefreshCookies () {
        HttpHeaders headers = new HttpHeaders();
        deleteCookie(accessCookieName, headers);
        deleteCookie(refreshCookieName, headers);
        return headers;
    }

    public HttpHeaders deleteTemporaryCookies () {
        HttpHeaders headers = new HttpHeaders();
        deleteCookie(temporaryCookieName, headers);
        return headers;
    }

    private ResponseCookie generateCookie(String name, String value, long maxAge) {
        return ResponseCookie.from(name, value)
                        .httpOnly(true)
                        .secure(secure)
                        .sameSite(sameSite)
                        .path("/")
                        .maxAge(maxAge)
                        .build();
    }

    private ResponseCookie generateCookie(String name, String value) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/")
                .build();
    }

    private void deleteCookie(String name, HttpHeaders headers) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .build();

        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
