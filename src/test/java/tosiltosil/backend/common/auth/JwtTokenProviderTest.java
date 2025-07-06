package tosiltosil.backend.common.auth;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tosiltosil.backend.common.auth.domain.response.TokenPair;
import tosiltosil.backend.common.auth.util.JwtUtil;
import tosiltosil.backend.common.domain.exception.UnauthorizedException;
import tosiltosil.backend.module.auth.infrastructure.RefreshTokenRedisRepository;
import tosiltosil.backend.module.auth.infrastructure.TemporaryTokenRedisRepository;

@SpringBootTest
@ActiveProfiles("test")
public class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenRedisRepository refreshTokenRedisRepository;

    @Autowired
    private TemporaryTokenRedisRepository temporaryTokenRedisRepository;

    private final UUID memberId = UUID.randomUUID();
    private final String email = "test@example.com";

    @AfterEach
    void afterEach() {
        refreshTokenRedisRepository.delete(memberId);
        temporaryTokenRedisRepository.delete(email);
    }

    @Test
    void 임시_토큰_생성을_성공한다() {
        // when
        String temporaryToken = jwtTokenProvider.createTemporaryToken(email);

        // then
        String savedTemporaryToken = temporaryTokenRedisRepository.get(email);
        assertThat(temporaryToken).isEqualTo(savedTemporaryToken);
    }

    @Test
    void 엑세스_토큰과_리프레스_토큰_생성을_성공한다() {
        // when
        TokenPair tokenPair = jwtTokenProvider.createTokens(memberId);
        String refreshToken = tokenPair.refreshToken();

        // then
        String savedRefreshToken = refreshTokenRedisRepository.get(memberId);
        assertThat(refreshToken).isEqualTo(savedRefreshToken);
    }

    @Test
    void 리프레스_토큰이_null값이면_재발급_요청_시_401_에러를_반환한다() {
        // given
        String invalidRefreshToken = null;

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.reissueTokens(invalidRefreshToken))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("잘못된 형식의 토큰입니다.");
    }

    @Test
    void redis에_저장된_토큰값과_다르면_redis_데이터를_삭제하고_401_에러를_반환한다() {
        // given
        String validRefreshToken = jwtTokenProvider.createTokens(memberId).refreshToken();
        refreshTokenRedisRepository.save(memberId, "another-refresh-token", 36000L);

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.retrieveRefreshToken(validRefreshToken))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("유효하지 않은 토큰입니다.");
    }

/* 엑세스 토큰의 만료 기한을 짧게 수정해야 되기 때문에 해당 테스트는 테스트 통과 확인 후 주석 처리했습니다.
    @Test
    void 만료된_엑세스_토큰을_파싱하면_401_에러를_반환한다() throws InterruptedException {
        // given
        String token = jwtUtil.generateAccessToken(memberId);

        // 임의로 AT 만료 시간을 360으로 설정하여 테스트를 진행함
        Thread.sleep(600);

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.retrieveAccessToken(token))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("토큰이 만료되었습니다.");
    }
 */

    @Test
    void 잘못된_형식의_엑세스_토큰을_파싱하면_401_에러를_반환한다() {
        // given
        String invalidAccessToken = "this.is.not.a.valid.access-token";

        // when & then
        assertThatThrownBy(() -> jwtTokenProvider.retrieveAccessToken(invalidAccessToken))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("잘못된 형식의 토큰입니다.");
    }
}
