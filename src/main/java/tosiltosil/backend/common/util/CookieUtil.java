package tosiltosil.backend.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.server.Cookie;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class CookieUtil {

    private final Environment environment;

    private static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    private static final String TEMPORARY_TOKEN_COOKIE_NAME = "temporary_token";

    public HttpHeaders generateAccessAndRefreshTokenCookies(String accessToken, String refreshToken) {
        ResponseCookie accessTokenCookie = generateCookie(ACCESS_TOKEN_COOKIE_NAME, accessToken);
        ResponseCookie refreshTokenCookie = generateCookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return headers;
    }

    public HttpHeaders generateAccessTokenCookies(String accessToken) {
        ResponseCookie accessTokenCookie = generateCookie(ACCESS_TOKEN_COOKIE_NAME, accessToken);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        return headers;
    }

    public HttpHeaders generateTemporaryTokenCookies(String temporaryToken) {
        ResponseCookie temporaryTokenCookie = generateCookie(TEMPORARY_TOKEN_COOKIE_NAME, temporaryToken);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, temporaryTokenCookie.toString());
        return headers;
    }

    private ResponseCookie generateCookie(String name, String value) {
        String sameSite = getCookieSameSite();
        boolean secure = isCookieSecure();

        return ResponseCookie.from(name, value)
                        .httpOnly(true)
                        .secure(secure)
                        .sameSite(sameSite)
                        .path("/")
                        .build();
    }


    private String getCookieSameSite() {
        if (Arrays.asList(environment.getActiveProfiles()).contains("prod")) {
            return Cookie.SameSite.STRICT.attributeValue();
        }
        return Cookie.SameSite.NONE.attributeValue();
    }

    private boolean isCookieSecure() {
        if (Arrays.asList(environment.getActiveProfiles()).contains("dev")) {
            return false;
        }
        return true;
    }
}
