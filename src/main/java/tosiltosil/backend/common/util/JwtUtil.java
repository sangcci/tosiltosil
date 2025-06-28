package tosiltosil.backend.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tosiltosil.backend.common.auth.domain.value.TokenType;
import tosiltosil.backend.common.auth.domain.response.AccessTokenInfo;
import tosiltosil.backend.common.auth.domain.response.RefreshTokenInfo;
import tosiltosil.backend.common.auth.domain.response.TemporaryTokenInfo;
import tosiltosil.backend.common.domain.exception.UnauthorizedException;
import tosiltosil.backend.common.properties.JwtProperties;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;

    private static final String TOKEN_TYPE = "type";
    private static final String CACHE_KEY = "cacheKey";

    public String generateTemporaryToken(UUID memberId, String cacheKey) {
        Date issuedAt = new Date();
        Date expiredAt = new Date(issuedAt.getTime() + jwtProperties.expiration().temporary());
        return buildTemporaryToken(memberId, cacheKey, issuedAt, expiredAt);
    }

    public String generateAccessToken(UUID memberId) {
        Date issuedAt = new Date();
        Date expiredAt = new Date(issuedAt.getTime() + jwtProperties.expiration().access());
        return buildAccessToken(memberId, issuedAt, expiredAt);
    }

    public String generateRefreshToken(UUID memberId) {
        Date issuedAt = new Date();
        Date expiredAt = new Date(issuedAt.getTime() + jwtProperties.expiration().refresh());
        return buildRefreshToken(memberId, issuedAt, expiredAt);
    }

    public TemporaryTokenInfo parseTemporaryToken(String token) throws ExpiredJwtException {
        Claims claims = getClaims(token, generateTemporarySecretKey());
        validateTokenType(TokenType.TEMPORARY.name(), claims);
        return TemporaryTokenInfo.from(claims);
    }

    public AccessTokenInfo parseAccessToken(String token) throws ExpiredJwtException {
        Claims claims = getClaims(token, generateAccessSecretKey());
        validateTokenType(TokenType.ACCESS.name(), claims);
        return AccessTokenInfo.from(claims);
    }

    public RefreshTokenInfo parseRefreshToken(String token) throws ExpiredJwtException {
        Claims claims = getClaims(token, generateRefreshSecretKey());
        validateTokenType(TokenType.REFRESH.name(), claims);
        return RefreshTokenInfo.from(claims);
    }

    private Claims getClaims(String token, SecretKey secretKey) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String buildTemporaryToken(UUID memberId, String cacheKey, Date issuedAt, Date expiredAt) {
        return Jwts.builder()
                .issuedAt(issuedAt)
                .subject(memberId.toString())
                .expiration(expiredAt)
                .claim(CACHE_KEY, cacheKey)
                .claim(TOKEN_TYPE, TokenType.TEMPORARY.name())
                .signWith(generateTemporarySecretKey())
                .compact();
    }

    private String buildAccessToken(UUID memberId, Date issuedAt, Date expiredAt) {
        return Jwts.builder()
                .issuedAt(issuedAt)
                .expiration(expiredAt)
                .subject(memberId.toString())
                .claim(TOKEN_TYPE, TokenType.ACCESS.name())
                .signWith(generateAccessSecretKey())
                .compact();
    }

    private String buildRefreshToken(UUID memberId, Date issuedAt, Date expiredAt) {
        return Jwts.builder()
                .issuedAt(issuedAt)
                .expiration(expiredAt)
                .subject(memberId.toString())
                .claim(TOKEN_TYPE, TokenType.REFRESH.name())
                .signWith(generateRefreshSecretKey())
                .compact();
    }

    private SecretKey generateSecretKey(String secretKey) {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    private SecretKey generateTemporarySecretKey() {
        return generateSecretKey(jwtProperties.temporaryTokenSecret());
    }

    private SecretKey generateAccessSecretKey() {
        return generateSecretKey(jwtProperties.accessTokenSecret());
    }

    private SecretKey generateRefreshSecretKey() {
        return generateSecretKey(jwtProperties.refreshTokenSecret());
    }

    private void validateTokenType(String validTokenType, Claims claims) {
        String tokenType = claims.get(TOKEN_TYPE, String.class);

        if(tokenType == null || !tokenType.equals(validTokenType))
            throw new UnauthorizedException("잘못된 Type의 토큰입니다.");
    }
}
