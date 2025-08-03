package tosiltosil.backend.module.email.presentation;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import tosiltosil.backend.common.domain.exception.BadRequestException;
import tosiltosil.backend.common.domain.exception.ConflictException;
import tosiltosil.backend.common.domain.exception.InvalidEmailCodeException;
import tosiltosil.backend.common.domain.exception.NotFoundException;
import tosiltosil.backend.common.util.CookieUtil;
import tosiltosil.backend.module.email.application.EmailService;
import tosiltosil.backend.module.email.domain.request.EmailAuthRequest;
import tosiltosil.backend.module.email.domain.request.EmailSendRequest;
import tosiltosil.backend.module.email.domain.response.EmailAuthResponse;
import tosiltosil.backend.module.email.domain.response.EmailSendResponse;
import tosiltosil.backend.support.RestDocsTestSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

@WebMvcTest(EmailController.class)
@SuppressWarnings("NonAsciiCharacters")
class EmailControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private CookieUtil cookieUtil;

    @Test
    void 인증_이메일_전송하기() {
        // given
        String request = """
                {
                    "email": "test@example.com",
                    "purpose": "SIGN_UP"
                }
                """;

        EmailSendResponse response = EmailSendResponse.of("test@example.com");

        given(emailService.sendAuthEmail(any(EmailSendRequest.class)))
                .willReturn(response);

        // when
        MvcTestResult testResult = mockMvcTester.post()
                .uri("/api/v1/auth/email/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .exchange();

        // then
        assertThat(testResult)
                .hasStatus(HttpStatus.OK)
                .apply(documentHandler.document(
                        requestFields(
                                requestField("email", JsonFieldType.STRING, "이메일 주소", false, "이메일 형식만 가능", "test@example.com"),
                                requestField("purpose", JsonFieldType.STRING, "이메일 인증 목적", false, "SIGN_UP 혹은 FORGOT_PASSWORD만 가능", "SIGN_UP")
                        ),
                        responseFields(
                                responseField("status", JsonFieldType.NUMBER, "응답 상태 코드", "200"),
                                responseField("message", JsonFieldType.STRING, "응답 메시지", "정상적으로 이메일을 전송했습니다."),
                                responseField("data.email", JsonFieldType.STRING, "인증 이메일이 전송된 이메일 주소", "test@example.com")
                        )
                ));
    }

    @Test
    void 이메일_전송_및_인증_시도_횟수_초과로_이메일_전송_실패() {
        // given
        String request = """
                {
                    "email": "test@example.com",
                    "purpose": "SIGN_UP"
                }
                """;

        given(emailService.sendAuthEmail(any(EmailSendRequest.class)))
                .willThrow(new BadRequestException("일일 이메일 인증 횟수를 초과하였습니다."));

        // when
        MvcTestResult testResult = mockMvcTester.post()
                .uri("/api/v1/auth/email/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .exchange();

        // then
        assertThat(testResult)
                .bodyJson().isEqualTo("""
                            {
                                "status": 400,
                                "message": "일일 이메일 인증 횟수를 초과하였습니다.",
                                "errors": []
                            }
                        """);

        assertThat(testResult)
                .apply(documentHandler.document());
    }

    @Test
    void 이메일_형식이_아닌_경우_실패() {
        // given
        String request = """
                {
                    "email": "testexample.com",
                    "purpose": "SIGN_UP"
                }
                """;

        given(emailService.sendAuthEmail(any(EmailSendRequest.class)))
                .willThrow(new BadRequestException("파라미터 값이 잘못되었습니다"));

        // when
        MvcTestResult testResult = mockMvcTester.post()
                .uri("/api/v1/auth/email/send")
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
    void 이메일_인증_목적이_SIGN_UP_혹은_FORGOT_PASSWORD이_아닌_경우_실패() {
        // given
        String request = """
                {
                    "email": "test@example.com",
                    "purpose": "NON_EXISTING_PURPOSE"
                }
                """;

        given(emailService.sendAuthEmail(any(EmailSendRequest.class)))
                .willThrow(new BadRequestException("파라미터 값이 유효하지 않습니다."));

        // when
        MvcTestResult testResult = mockMvcTester.post()
                .uri("/api/v1/auth/email/send")
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
                                        "field": "purpose",
                                        "value": "NON_EXISTING_PURPOSE",
                                        "reason": "파라미터 값이 유효하지 않습니다."
                                    }
                                ]
                            }
                        """);

        assertThat(testResult)
                .apply(documentHandler.document());
    }

    @Test
    void 가입되지_않은_이메일로_비밀번호_찾기_인증_이메일_요청_시_실패() {
        // given
        String request = """
                {
                    "email": "test@example.com",
                    "purpose": "FORGOT_PASSWORD"
                }
                """;

        given(emailService.sendAuthEmail(any(EmailSendRequest.class)))
                .willThrow(new BadRequestException("등록되지 않은 이메일입니다."));


        // when
        MvcTestResult testResult = mockMvcTester.post()
                .uri("/api/v1/auth/email/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .exchange();

        // then
        assertThat(testResult)
                .bodyJson().isEqualTo("""
                            {
                                "status": 400,
                                "message": "등록되지 않은 이메일입니다.",
                                "errors": []
                            }
                        """);

        assertThat(testResult)
                .apply(documentHandler.document());
    }

    @Test
    void 가입된_이메일로_회원가입_인증_이메일_요청_시_실패() {
        // given
        String request = """
                {
                    "email": "test@example.com",
                    "purpose": "SIGN_UP"
                }
                """;

        given(emailService.sendAuthEmail(any(EmailSendRequest.class)))
                .willThrow(new ConflictException("이미 등록된 이메일입니다."));


        // when
        MvcTestResult testResult = mockMvcTester.post()
                .uri("/api/v1/auth/email/send")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .exchange();

        // then
        assertThat(testResult)
                .bodyJson().isEqualTo("""
                            {
                                "status": 409,
                                "message": "이미 등록된 이메일입니다.",
                                "errors": []
                            }
                        """);

        assertThat(testResult)
                .apply(documentHandler.document());
    }

    @Test
    void 인증_번호_검증하기() {
        // given
        String request = """
                {
                    "email": "test@example.com",
                    "authNumber": "123456"
                }
                """;

        String temporaryToken = "temporary-token"; // 임시 토큰 예시

        given(emailService.verifyAuthEmail(any(EmailAuthRequest.class)))
                .willReturn(EmailAuthResponse.of(temporaryToken));

        HttpHeaders mockHeaders = new HttpHeaders();
        mockHeaders.add(HttpHeaders.SET_COOKIE, "temporary-token=" + temporaryToken + "; Path=/; HttpOnly; Max-Age=600000");

        given(cookieUtil.generateTemporaryTokenCookies(any(String.class)))
                .willReturn(mockHeaders);

        // when
        MvcTestResult testResult = mockMvcTester.post()
                .uri("/api/v1/auth/email/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .exchange();

        // then
        assertThat(testResult)
                .hasStatus(HttpStatus.OK)
                .apply(documentHandler.document(
                        responseCookies(
                                cookieWithName("temporary-token").description("임시 엑세스 토큰 쿠키")
                        ),
                        requestFields(
                                requestField("email", JsonFieldType.STRING, "이메일 주소", false, "이메일 형식만 가능", "test@example.com"),
                                requestField("authNumber", JsonFieldType.STRING, "인증번호", false, "6자리 숫자만 가능", "123456")
                        ),
                        responseFields(
                                responseField("status", JsonFieldType.NUMBER, "응답 상태 코드", "200"),
                                responseField("message", JsonFieldType.STRING, "응답 메시지", "이메일 인증이 완료되었습니다."),
                                responseField("data", JsonFieldType.OBJECT, "응답 데이터", "{}")
                        )
                ));
    }

    @Test
    void 이메일_인증_시도_횟수_초과로_인증번호_검증_실패() {
        // given
        String request = """
                {
                    "email": "test@example.com",
                    "authNumber": "123456"
                }
                """;

        given(emailService.verifyAuthEmail(any(EmailAuthRequest.class)))
                .willThrow(new BadRequestException("일일 이메일 인증 횟수를 초과하였습니다."));

        // when
        MvcTestResult testResult = mockMvcTester.post()
                .uri("/api/v1/auth/email/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .exchange();

        // then
        assertThat(testResult)
                .bodyJson().isEqualTo("""
                            {
                                "status": 400,
                                "message": "일일 이메일 인증 횟수를 초과하였습니다.",
                                "errors": []
                            }
                        """);

        assertThat(testResult)
                .apply(documentHandler.document());
    }

    @Test
    void 잘못된_형식의_인증_번호에_검증_실패() {
        // given
        String request = """
                {
                    "email": "test@example.com",
                    "authNumber": "1234526"
                }
                """;

        given(emailService.verifyAuthEmail(any(EmailAuthRequest.class)))
                .willThrow(new BadRequestException("파라미터 값이 잘못되었습니다"));

        // when
        MvcTestResult testResult = mockMvcTester.post()
                .uri("/api/v1/auth/email/verify")

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
                                        "field": "authNumber",
                                        "value": "1234526",
                                        "reason": "인증번호는 6자리 숫자로 입력해주세요."
                                    }
                                ]
                            }
                        """);

        assertThat(testResult)
                .apply(documentHandler.document());
    }

    @Test
    void 잘못된_인증_번호에_검증_실패() {
        // given
        String request = """
                {
                    "email": "test@example.com",
                    "authNumber": "125526"
                }
                """;

        int currentFailCount = 1;

        given(emailService.verifyAuthEmail(any(EmailAuthRequest.class)))
                .willThrow(new InvalidEmailCodeException(currentFailCount));

        // when
        MvcTestResult testResult = mockMvcTester.post()
                .uri("/api/v1/auth/email/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .exchange();

        // then
        assertThat(testResult)
                .bodyJson().isEqualTo("""
                            {
                                "status": 400,
                                "message": "잘못된 인증번호입니다.",
                                "errors": [
                                    {
                                        "field": "failCount",
                                        "value": "1",
                                        "reason": "금일 총 1회 틀렸습니다. 하루 최대 5회까지 가능합니다."
                                    }
                                ]
                            }
                        """);

        assertThat(testResult)
                .apply(documentHandler.document());
    }

    @Test
    void 인증번호_만료로_검증_실패() {
        // given
        String request = """
                {
                    "email": "test@example.com",
                    "authNumber": "125526"
                }
                """;

        given(emailService.verifyAuthEmail(any(EmailAuthRequest.class)))
                .willThrow(new NotFoundException("인증 유효 시간이 만료되었거나, 잘못된 인증 요청입니다."));

        // when
        MvcTestResult testResult = mockMvcTester.post()
                .uri("/api/v1/auth/email/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .exchange();

        // then
        assertThat(testResult)
                .bodyJson().isEqualTo("""
                            {
                                "status": 404,
                                "message": "인증 유효 시간이 만료되었거나, 잘못된 인증 요청입니다.",
                                "errors": []
                            }
                        """);

        assertThat(testResult)
                .apply(documentHandler.document());
    }
}