package tosiltosil.backend.common.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tosiltosil.backend.common.auth.domain.response.AccessTokenInfo;
import tosiltosil.backend.common.auth.domain.response.RefreshTokenInfo;
import tosiltosil.backend.common.auth.domain.response.TemporaryTokenInfo;
import tosiltosil.backend.common.auth.domain.value.TokenType;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.secret-key.temporary}")
    private String temporaryTokenKey;

    @Value("${jwt.secret-key.access}")
    private String accessTokenKey;

    @Value("${jwt.secret-key.refresh}")
    private String refreshTokenKey;

    @Value("${jwt.expiration.temporary}")
    private long temporaryTokenExpiration;

    @Value("${jwt.expiration.access}")
    private long accessTokenExpiration;

    @Value("${jwt.expiration.refresh}")
    private long refreshTokenExpiration;

    private static final String TOKEN_TYPE = "type";

    public String generateTemporaryToken(String email) {
        Date issuedAt = new Date();
        Date expiredAt = new Date(issuedAt.getTime() + temporaryTokenExpiration);
        return buildTemporaryToken(email, issuedAt, expiredAt);
    }

    public String generateAccessToken(UUID memberId) {
        Date issuedAt = new Date();
        Date expiredAt = new Date(issuedAt.getTime() + accessTokenExpiration);
        return buildAccessToken(memberId, issuedAt, expiredAt);
    }

    public String generateRefreshToken(UUID memberId) {
        Date issuedAt = new Date();
        Date expiredAt = new Date(issuedAt.getTime() + refreshTokenExpiration);
        return buildRefreshToken(memberId, issuedAt, expiredAt);
    }

    public TemporaryTokenInfo parseTemporaryToken(String token) throws ExpiredJwtException {
        Claims claims = getClaims(token, generateTemporarySecretKey());
        return TemporaryTokenInfo.from(token, claims);
    }

    public AccessTokenInfo parseAccessToken(String token) throws ExpiredJwtException {
        Claims claims = getClaims(token, generateAccessSecretKey());
        return AccessTokenInfo.from(token, claims);
    }

    public RefreshTokenInfo parseRefreshToken(String token) throws ExpiredJwtException {
        Claims claims = getClaims(token, generateRefreshSecretKey());
        return RefreshTokenInfo.from(token, claims);
    }

    public Long getRefreshTokenExpiration(String token) {
        Claims claims = getClaims(token, generateRefreshSecretKey());
        return claims.getExpiration().getTime();
    }

    public Long getTemporaryTokenExpiration(String token) {
        Claims claims = getClaims(token, generateTemporarySecretKey());
        return claims.getExpiration().getTime();
    }

    private Claims getClaims(String token, SecretKey secretKey) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String buildTemporaryToken(String email, Date issuedAt, Date expiredAt) {
        return Jwts.builder()
                .issuedAt(issuedAt)
                .expiration(expiredAt)
                .subject(email)
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
        return generateSecretKey(temporaryTokenKey);
    }

    private SecretKey generateAccessSecretKey() {
        return generateSecretKey(accessTokenKey);
    }

    private SecretKey generateRefreshSecretKey() {
        return generateSecretKey(refreshTokenKey);
    }
}
