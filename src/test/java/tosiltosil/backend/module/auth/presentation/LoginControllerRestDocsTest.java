package tosiltosil.backend.module.auth.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import tosiltosil.backend.common.domain.exception.BadRequestException;
import tosiltosil.backend.common.domain.exception.UnauthorizedException;
import tosiltosil.backend.common.util.CookieUtil;
import tosiltosil.backend.module.auth.application.AuthService;
import tosiltosil.backend.module.auth.domain.request.LocalLoginRequest;
import tosiltosil.backend.module.auth.domain.response.LocalLoginResponse;
import tosiltosil.backend.support.RestDocsTestSupport;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

@WebMvcTest(AuthController.class)
@SuppressWarnings("NonAsciiCharacters")
public class LoginControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private CookieUtil cookieUtil;

    @Value("${jwt.expiration.access}")
    private int accessTokenExpirationTime;

    @Value("${jwt.expiration.refresh}")
    private int refreshTokenExpirationTime;

    @Test
    void 일반_로그인하기() {
        // given
        String request = """
            {
                "email": "test@example.com",
                "password": "qwer1234!"
            }
        """;

        UUID memberId = UUID.randomUUID();
        String accessToken = "access-token";
        String refreshToken = "refresh-token";

        given(authService.localLogin(any(LocalLoginRequest.class)))
                .willReturn(LocalLoginResponse.of(memberId, accessToken, refreshToken));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, "access-token=" + accessToken + "; Path=/; HttpOnly; Max-Age=" + accessTokenExpirationTime);
        headers.add(HttpHeaders.SET_COOKIE, "refresh-token=" + refreshToken + "; Path=/; HttpOnly; Max-Age=" + refreshTokenExpirationTime);

        given(cookieUtil.generateAccessAndRefreshTokenCookies(any(String.class), any(String.class)))
                .willReturn(headers);

        // when
        MvcTestResult testResult = mockMvcTester.post()
                .uri("/api/v1/auth/login/local")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .exchange();

        // then
        assertThat(testResult)
                .hasStatus(HttpStatus.OK)
                .apply(documentHandler.document(
                        responseCookies(
                                cookieWithName("access-token").description("엑세스 토큰 쿠키"),
                                cookieWithName("refresh-token").description("리프레시 토큰 쿠키")
                        ),
                        requestFields(
                                requestField("email", JsonFieldType.STRING, "이메일 주소", false, "이메일 형식만 가능", "test@example.com"),
                                requestField("password", JsonFieldType.STRING, "비밀번호", false, "영문, 숫자, 특수문자를 포함하여 8글자 이상으로 입력 가능", "qwer1234!")
                        ),
                        responseFields(
                                responseField("status", JsonFieldType.NUMBER, "응답 상태 코드", "200"),
                                responseField("message", JsonFieldType.STRING, "응답 메시지", "정상적으로 로그인 되었습니다."),
                                responseField("data", JsonFieldType.OBJECT, "응답 데이터", "{}"),
                                responseField("data.memberId", JsonFieldType.STRING, "로그인한 사용자의 ID", memberId.toString())
                        )
                ));
    }

    @Test
    void 이메일_혹은_비밀번호_누락_시_로그인_실패() throws UnsupportedEncodingException, JsonProcessingException {
        // given
        String request = """
                {
                    "email": "",
                    "password": ""
                }
                """;

        given(authService.localLogin(any(LocalLoginRequest.class)))
                .willThrow(new BadRequestException("파라미터 값이 잘못되었습니다"));

        // when
        MvcTestResult testResult = mockMvcTester.post()
                .uri("/api/v1/auth/login/local")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .exchange();

        // then
        ObjectMapper objectMapper = new ObjectMapper();

        // JSON 응답 본문을 Map으로 변환
        String responseBody = testResult.getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(responseBody, new TypeReference<>() {});

        // status, message 등 고정 값 검증
        Assertions.assertThat(responseMap.get("status")).isEqualTo(400);
        Assertions.assertThat(responseMap.get("message")).isEqualTo("파라미터 값이 잘못되었습니다");

        // errors 배열을 List<Map>으로 추출하여 순서와 상관없이 내용 검증
        List<Map<String, String>> errors = (List<Map<String, String>>) responseMap.get("errors");

        assertThat(errors).containsExactlyInAnyOrder(
                Map.of("field", "email", "value", "", "reason", "이메일을 입력해주세요."),
                Map.of("field", "password", "value", "", "reason", "비밀번호를 입력해주세요."),
                Map.of("field", "password", "value", "", "reason", "비밀번호는 영문, 숫자, 특수문자를 포함하여 8글자 이상으로 입력해주세요.")
        );

        assertThat(testResult)
                .apply(documentHandler.document());
    }

    @Test
    void 이메일_형식이_아닌_경우_로그인_실패() {
        // given
        String request = """
                {
                    "email": "testexample.com",
                    "password": "qwer1234!"
                }
                """;

        given(authService.localLogin(any(LocalLoginRequest.class)))
                .willThrow(new BadRequestException("파라미터 값이 잘못되었습니다"));

        // when
        MvcTestResult testResult = mockMvcTester.post()
                .uri("/api/v1/auth/login/local")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .exchange();

        // then
        assertThat(testResult)
                .bodyJson().isEqualTo("""
                            {
                                "status": 400,
                                "message": "파라미터 값이 잘못되었습니다",
                                "errors": [
                                    {
                                        "field": "email",
                                        "value": "testexample.com",
                                        "reason": "이메일 형식이 아닙니다."
                                    }
                                ]
                            }
                        """);

        assertThat(testResult)
                .apply(documentHandler.document());
    }

    @Test
    void 일치하지_않은_이메일_혹은_비밀번호로_시도_시_로그인_실패() {
        // given
        String request = """
            {
                "email": "test@example.com",
                "password": "abcd0987^"
            }
        """;

        given(authService.localLogin(any(LocalLoginRequest.class)))
                .willThrow(new UnauthorizedException("이메일 또는 비밀번호가 올바르지 않습니다."));

        // when
        MvcTestResult testResult = mockMvcTester.post()
                .uri("/api/v1/auth/login/local")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .exchange();

        // then
        assertThat(testResult)
                .bodyJson().isEqualTo("""
                            {
                                "status": 401,
                                "message": "이메일 또는 비밀번호가 올바르지 않습니다.",
                                "errors": []
                            }
                        """);

        assertThat(testResult)
                .apply(documentHandler.document());
    }

}
