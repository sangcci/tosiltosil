package tosiltosil.backend.common.auth;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tosiltosil.backend.common.auth.domain.response.AccessTokenInfo;
import tosiltosil.backend.common.auth.domain.response.RefreshTokenInfo;
import tosiltosil.backend.common.auth.domain.response.TemporaryTokenInfo;
import tosiltosil.backend.common.auth.domain.response.TokenPair;
import tosiltosil.backend.common.auth.util.JwtUtil;
import tosiltosil.backend.common.domain.exception.UnauthorizedException;
import tosiltosil.backend.module.auth.infrastructure.TemporaryTokenRedisRepository;
import tosiltosil.backend.module.auth.infrastructure.RefreshTokenRedisRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final TemporaryTokenRedisRepository temporaryTokenRedisRepository;

    public String createTemporaryToken(String email) {
        String temporaryToken = jwtUtil.generateTemporaryToken(email);
        saveTemporaryTokenToRedis(email, temporaryToken);
        return temporaryToken;
    }

    public TokenPair createTokens(UUID memberId) {
        String newRefreshToken = createRefreshToken(memberId);
        String newAccessToken = createAccessToken(memberId);
        return TokenPair.of(newAccessToken, newRefreshToken);
    }

    public TokenPair reissueTokens(String refreshToken) {
        RefreshTokenInfo refreshTokenInfo = retrieveRefreshToken(refreshToken);
        return createTokens(refreshTokenInfo.memberId());
    }

    public TemporaryTokenInfo retrieveTemporaryToken(String temporaryToken) {
        try {
            TemporaryTokenInfo tokenInfo = jwtUtil.parseTemporaryToken(temporaryToken);
            String redisToken = getTemporaryTokenFromRedis(tokenInfo.email());

            if (redisToken == null || !redisToken.equals(tokenInfo.token())) {
                deleteTemporaryTokenFromRedis(tokenInfo.email());
                throw new UnauthorizedException("유효하지 않은 토큰입니다.");
            }

            return tokenInfo;

        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("토큰이 만료되었습니다.");
        } catch (MalformedJwtException | IllegalArgumentException e) {
            throw new UnauthorizedException("잘못된 형식의 토큰입니다.");
        }
    }

    public AccessTokenInfo retrieveAccessToken(String accessToken) {
        try {
            return jwtUtil.parseAccessToken(accessToken);
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("토큰이 만료되었습니다.");
        } catch (MalformedJwtException | IllegalArgumentException e) {
            throw new UnauthorizedException("잘못된 형식의 토큰입니다.");
        }
    }

    public RefreshTokenInfo retrieveRefreshToken(String refreshToken) {
        try {
            RefreshTokenInfo tokenInfo = jwtUtil.parseRefreshToken(refreshToken);
            String redisToken = getRefreshTokenFromRedis(tokenInfo.memberId());

            if (redisToken == null || !redisToken.equals(tokenInfo.token())) {
                deleteRefreshTokenFromRedis(tokenInfo.memberId());
                throw new UnauthorizedException("유효하지 않은 토큰입니다.");
            }

            return tokenInfo;
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("토큰이 만료되었습니다.");
        } catch (MalformedJwtException | IllegalArgumentException e) {
            throw new UnauthorizedException("잘못된 형식의 토큰입니다.");
        }
    }

    private String createAccessToken(UUID memberId)  {
        return jwtUtil.generateAccessToken(memberId);
    }

    private String createRefreshToken(UUID memberId) {
        String refreshToken = jwtUtil.generateRefreshToken(memberId);
        saveRefreshTokenToRedis(memberId, refreshToken);
        return refreshToken;
    }

    private long calculateTtl(long expiration) {
        return expiration - System.currentTimeMillis();
    }

    private void saveTemporaryTokenToRedis(String email, String temporaryToken) {
        Long expiration = jwtUtil.getTemporaryTokenExpiration(temporaryToken);
        long ttl = calculateTtl(expiration);

        temporaryTokenRedisRepository.save(email, temporaryToken, ttl);
    }

    private void saveRefreshTokenToRedis(UUID memberId, String refreshToken) {
        Long expiration = jwtUtil.getRefreshTokenExpiration(refreshToken);
        long ttl = calculateTtl(expiration);

        refreshTokenRedisRepository.save(memberId, refreshToken, ttl);
    }

    private String getTemporaryTokenFromRedis(String email) {
        return temporaryTokenRedisRepository.get(email);
    }

    private String getRefreshTokenFromRedis(UUID memberId) {
        return refreshTokenRedisRepository.get(memberId);
    }

    private void deleteTemporaryTokenFromRedis(String email) {
        temporaryTokenRedisRepository.delete(email);
    }

    private void deleteRefreshTokenFromRedis(UUID memberId) {
        refreshTokenRedisRepository.delete(memberId);
    }
}
