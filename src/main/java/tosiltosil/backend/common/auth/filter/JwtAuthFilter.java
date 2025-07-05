package tosiltosil.backend.common.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;
import tosiltosil.backend.common.auth.AuthDetails;
import tosiltosil.backend.common.auth.JwtTokenProvider;
import tosiltosil.backend.common.auth.domain.response.TokenPair;
import tosiltosil.backend.common.domain.exception.UnauthorizedException;
import tosiltosil.backend.common.util.CookieUtil;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CookieUtil cookieUtil;

    @Value("${jwt.cookie.name.access}")
    private String ACCESS_TOKEN_COOKIE_NAME;

    @Value("${jwt.cookie.name.refresh}")
    private String REFRESH_TOKEN_COOKIE_NAME;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {
        String accessToken = extractAccessTokenFromCookie(request);
        String refreshToken = extractRefreshTokenFromCookie(request);

        if (accessToken == null || refreshToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // AT & RT 재발급
            TokenPair reissuedTokenPair = jwtTokenProvider.reissueTokens(refreshToken);
            String reissuedAccessToken = reissuedTokenPair.accessToken();
            String reissuedRefreshToken = reissuedTokenPair.refreshToken();

            // 쿠키에 저장
            HttpHeaders headers = cookieUtil.generateAccessAndRefreshTokenCookies(reissuedAccessToken, reissuedRefreshToken);
            headers.forEach((name, values) ->
                    values.forEach(value -> response.addHeader(name, value)));

            // memberId을 추출하기 위함
            UUID memberId = jwtTokenProvider.retrieveAccessToken(reissuedAccessToken).memberId();
            setAuthenticationToContext(memberId);
        } catch (UnauthorizedException e) {
            // 예외가 발생하면 AT & RT 쿠키 삭제
            HttpHeaders deleteHeaders = cookieUtil.deleteAccessAndRefreshCookies();

            deleteHeaders.forEach((name, values) ->
                    values.forEach(value -> response.addHeader(name, value)));

            throw e;
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthenticationToContext(UUID memberId) {
        UserDetails authDetails = new AuthDetails(memberId);

        Authentication authentication = new UsernamePasswordAuthenticationToken(authDetails, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String extractAccessTokenFromCookie(HttpServletRequest request) {
        return Optional.ofNullable(WebUtils.getCookie(request, ACCESS_TOKEN_COOKIE_NAME))
                .map(Cookie::getValue)
                .orElse(null);
    }

    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        return Optional.ofNullable(WebUtils.getCookie(request, REFRESH_TOKEN_COOKIE_NAME))
                .map(Cookie::getValue)
                .orElse(null);
    }
}