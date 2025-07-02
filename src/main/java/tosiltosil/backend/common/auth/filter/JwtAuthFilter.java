package tosiltosil.backend.common.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;
import tosiltosil.backend.common.auth.JwtTokenProvider;
import tosiltosil.backend.common.auth.AuthDetails;
import tosiltosil.backend.common.auth.domain.response.AccessTokenInfo;
import tosiltosil.backend.common.auth.domain.response.RefreshTokenInfo;
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

    private static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

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
            // RT가 유효한지 확인 (null 이면 만료됨)
            RefreshTokenInfo refreshTokenInfo = jwtTokenProvider.retrieveRefreshToken(refreshToken);

            // AT가 유효한 경우 다음 필터로 넘어감
            setAuthenticationToContext(refreshTokenInfo.memberId());
            filterChain.doFilter(request, response);

            return;
        } catch (UnauthorizedException e) {
            // RT 만료된 경우 AT & RT 재발급
            TokenPair reissuedTokenPair = jwtTokenProvider.reissueAllToken(accessToken, refreshToken);
            String reissuedAccessToken = reissuedTokenPair.accessToken();
            String reissuedRefreshToken = reissuedTokenPair.refreshToken();

            // memberId을 추출하기 위함
            AccessTokenInfo accessTokenInfo = jwtTokenProvider.retrieveAccessToken(reissuedAccessToken);

            // 쿠키에 저장
            HttpHeaders headers = cookieUtil.generateAccessAndRefreshTokenCookies(reissuedAccessToken, reissuedRefreshToken);
            headers.forEach((name, values) ->
                    values.forEach(value -> response.addHeader(name, value)));

            setAuthenticationToContext(accessTokenInfo.memberId());
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