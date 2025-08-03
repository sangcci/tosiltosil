package tosiltosil.backend.module.auth.presentation;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import tosiltosil.backend.common.auth.domain.response.TokenPair;
import tosiltosil.backend.common.domain.exception.BadRequestException;
import tosiltosil.backend.common.util.CookieUtil;
import tosiltosil.backend.module.auth.application.AuthService;
import tosiltosil.backend.support.RestDocsTestSupport;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.cookies.CookieDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

@WebMvcTest(AuthController.class)
@SuppressWarnings("NonAsciiCharacters")
public class ReissueControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private CookieUtil cookieUtil;

    @Value("${jwt.expiration.access}")
    private int accessTokenExpirationTime;

    @Value("${jwt.expiration.refresh}")
    private int refreshTokenExpirationTime;

    @Test
    void 엑세스와_리프레시_토큰_재발급() {
        // given
        String exRefreshToken = "ex-refresh-token";
        String newAccessToken = "new-access-token";
        String newRefreshToken = "new-refresh-token";
        TokenPair tokenPair = new TokenPair(newAccessToken, newRefreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE,  "access-token=" + newAccessToken + "; Path=/; HttpOnly; Max-Age=" + accessTokenExpirationTime);
        headers.add(HttpHeaders.SET_COOKIE,  "refresh-token=" + newRefreshToken + "; Path=/; HttpOnly; Max-Age=" + refreshTokenExpirationTime);

        given(authService.reissueTokens(exRefreshToken)).willReturn(tokenPair);
        given(cookieUtil.generateAccessAndRefreshTokenCookies(any(String.class), any(String.class)))
                .willReturn(headers);

        // when
        MvcTestResult testResult = mockMvcTester.get()
                .uri("/api/v1/auth/reissue")
                .cookie(new Cookie("refresh-token", exRefreshToken))
                .exchange();

        // then
        assertThat(testResult)
                .hasStatus(HttpStatus.OK)
                .apply(documentHandler.document(
                        requestCookies(
                                cookieWithName("refresh-token").description("이전 리프레시 토큰 쿠키")
                        ),
                        responseCookies(
                                cookieWithName("access-token").description("엑세스 토큰 쿠키"),
                                cookieWithName("refresh-token").description("리프레시 토큰 쿠키")
                        ),
                        responseFields(
                                responseField("status", JsonFieldType.NUMBER, "응답 상태 코드", "200"),
                                responseField("message", JsonFieldType.STRING, "응답 메시지", "정상적으로 토큰을 재발급했습니다."),
                                responseField("data", JsonFieldType.OBJECT, "응답 데이터", "{}")
                        )
                ));
    }

    @Test
    void 리프레시_토큰_누락_시_리프레시_토큰_재발급_실패() {
        // given
        given(authService.reissueTokens(isNull(String.class)))
                .willThrow(new BadRequestException("필수 쿠키가 누락되었습니다."));

        // when
        MvcTestResult testResult = mockMvcTester.get()
                .uri("/api/v1/auth/reissue")
                .exchange();

        // then
        assertThat(testResult)
                .bodyJson().isEqualTo("""
                            {
                                "status": 400,
                                "message": "필수 쿠키가 누락되었습니다.",
                                "errors": [
                                    {
                                        "field": "refresh-token",
                                        "value": null,
                                        "reason": "쿠키가 존재하지 않습니다."
                                    }
                                ]
                            }
                        """);

        assertThat(testResult)
                .apply(documentHandler.document());
    }
}
