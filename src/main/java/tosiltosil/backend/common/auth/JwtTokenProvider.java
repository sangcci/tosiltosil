package tosiltosil.backend.common.auth;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tosiltosil.backend.common.auth.domain.response.AccessTokenInfo;
import tosiltosil.backend.common.auth.domain.response.RefreshTokenInfo;
import tosiltosil.backend.common.auth.domain.response.TemporaryTokenInfo;
import tosiltosil.backend.common.auth.domain.response.TokenPair;
import tosiltosil.backend.common.domain.exception.UnauthorizedException;
import tosiltosil.backend.common.auth.util.JwtUtil;
import tosiltosil.backend.module.auth.infrastructure.EmailRedisRepository;
import tosiltosil.backend.module.auth.infrastructure.RefreshTokenRedisRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final EmailRedisRepository emailRedisRepository;

    private static final long RENEWAL_THRESHOLD = 24 * 60 * 60 * 1000L; // 24시간 (하루)

    public String createTemporaryToken(String email) {
        UUID cacheKey = saveEmailToRedis(email);
        return jwtUtil.generateTemporaryToken(cacheKey);
    }

    public String reissueAccessToken(String refreshToken) {
        RefreshTokenInfo tokenInfo = retrieveRefreshToken(refreshToken);
        return createAccessToken(tokenInfo.memberId());
    }

    public TokenPair reissueAllToken(String accessToken, String refreshToken) {

        RefreshTokenInfo tokenInfo = retrieveRefreshToken(refreshToken);
        UUID memberId = tokenInfo.memberId();

        long now = System.currentTimeMillis();
        long expiration = getRefreshTokenExpirationTime(refreshToken);

        if (expiration - now < RENEWAL_THRESHOLD) {
            deleteRefreshTokenFromRedis(memberId);
            return createTokenPair(memberId);
        }

        String newAccessToken = createAccessToken(memberId);
        return TokenPair.of(newAccessToken, refreshToken);
    }

    public TokenPair createTokenPair(UUID memberId) {
        String accessToken = createAccessToken(memberId);
        String refreshToken = createRefreshToken(memberId);
        return TokenPair.of(accessToken, refreshToken);
    }

    public String retrieveEmail(String temporaryToken) {
        TemporaryTokenInfo tokenInfo = jwtUtil.parseTemporaryToken(temporaryToken);
        String email = getEmailFromRedis(tokenInfo.key());

        if (email == null)
            throw new UnauthorizedException("올바르지 않은 임의 토큰입니다.");

        return email;
    }

    public AccessTokenInfo retrieveAccessToken(String accessToken) {
        try {
            AccessTokenInfo token = jwtUtil.parseAccessToken(accessToken);

            if (token == null)
                throw new UnauthorizedException("유효하지 않은 엑세스 토큰입니다.");

            return token;
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("만료된 엑세스 토큰입니다.");
        }
    }

    public RefreshTokenInfo retrieveRefreshToken(String refreshToken) {
        try {
            RefreshTokenInfo tokenInfo = jwtUtil.parseRefreshToken(refreshToken);
            String redisToken = getRefreshTokenFromRedis(tokenInfo.memberId());

            if (redisToken == null || !redisToken.equals(tokenInfo.token())) {
                throw new UnauthorizedException("유효하지 않은 리프레시 토큰입니다.");
            }

            return tokenInfo;
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("만료된 리프레시 토큰입니다.");
        }
    }

    public UUID saveEmailToRedis(String email) {
        UUID cacheKey = UUID.randomUUID() ;

        Long expiration = jwtUtil.getTemporaryTokenExpiration(email);
        long now = System.currentTimeMillis();
        long ttl = expiration - now;

        emailRedisRepository.save(cacheKey, email, ttl);

        return cacheKey;
    }

    public void saveRefreshTokenToRedis(UUID memberId, String refreshToken) {
        Long expiration = jwtUtil.getRefreshTokenExpiration(refreshToken);
        long now = System.currentTimeMillis();
        long ttl = expiration - now;

        refreshTokenRedisRepository.save(memberId, refreshToken, ttl);
    }

    private String createAccessToken(UUID memberId)  {
        return jwtUtil.generateAccessToken(memberId);
    }

    private String createRefreshToken(UUID memberId) {
        String refreshToken = jwtUtil.generateRefreshToken(memberId);
        saveRefreshTokenToRedis(memberId, refreshToken);
        return refreshToken;
    }

    private Long getRefreshTokenExpirationTime(String refreshToken) {
        return jwtUtil.getRefreshTokenExpiration(refreshToken);
    }

    private String getEmailFromRedis(UUID cacheKey) {
        return emailRedisRepository.get(cacheKey);
    }

    private String getRefreshTokenFromRedis(UUID memberId) {
        return refreshTokenRedisRepository.get(memberId);
    }

    private void deleteEmailFromRedis(UUID cacheKey) {
        emailRedisRepository.delete(cacheKey);
    }

    private void deleteRefreshTokenFromRedis(UUID memberId) {
        refreshTokenRedisRepository.delete(memberId);
    }
}
