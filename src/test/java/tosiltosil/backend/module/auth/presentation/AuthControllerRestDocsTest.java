package tosiltosil.backend.module.auth.presentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.web.multipart.MultipartFile;
import tosiltosil.backend.common.auth.domain.response.TokenPair;
import tosiltosil.backend.common.domain.exception.BadRequestException;
import tosiltosil.backend.common.domain.exception.UnauthorizedException;
import tosiltosil.backend.common.util.CookieUtil;
import tosiltosil.backend.module.auth.application.AuthService;
import tosiltosil.backend.module.auth.domain.request.CreateLocalMemberRequest;
import tosiltosil.backend.module.auth.domain.request.LocalLoginRequest;
import tosiltosil.backend.module.auth.domain.response.CreateLocalMemberResponse;
import tosiltosil.backend.module.auth.domain.response.LocalLoginResponse;
import tosiltosil.backend.support.RestDocsTestSupport;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.cookies.CookieDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@SuppressWarnings("NonAsciiCharacters")
public class AuthControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private CookieUtil cookieUtil;

    @Value("${jwt.expiration.access}")
    private int accessTokenExpirationTime;

    @Value("${jwt.expiration.refresh}")
    private int refreshTokenExpirationTime;

    @Test
    void 일반_회원가입하기() throws Exception {
        // given
        String memberInfoJson = """
            {
                "password": "qwer1234!",
                "nickname": "유저1",
                "terms": [
                    {"title": "termsOfService", "version": "0.1.0", "agreed": true},
                    {"title": "privacyPolicy", "version": "0.1.0", "agreed": true},
                    {"title": "ageConfirmation", "version": "0.1.0", "agreed": true}
                ]
            }
        """;

        MockMultipartFile memberInfo = new MockMultipartFile(
                "memberInfo",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                memberInfoJson.getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile profileImage = new MockMultipartFile(
                "profileImage",
                "image.jpeg",
                MediaType.IMAGE_JPEG_VALUE,
                "sample image data".getBytes(StandardCharsets.UTF_8)
        );

        CreateLocalMemberResponse response = CreateLocalMemberResponse.of("유저1");

        given(authService.localSignUp(any(String.class), any(CreateLocalMemberRequest.class), any(MultipartFile.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(
                multipart("/api/v1/auth/signup/local")
                        .file(memberInfo)
                        .file(profileImage)
                        .cookie(new Cookie("temporary-token", "temporary-token-value"))
        )
                .andExpect(status().isCreated())
                .andDo(
                        documentHandler.document(
                                requestParts(
                                        requestPart("memberInfo", "JSON", "사용자 가입 정보", false),
                                        requestPart("profileImage", "JPEG, PNG 및 최대 500KB 허용", "프로필 이미지 파일", true)
                                ),
                                requestPartFields(
                                        "memberInfo",
                                        requestField("password", JsonFieldType.STRING, "비밀번호", false, "영문, 숫자, 특수문자를 포함하여 8글자 이상으로 입력", "qwer1234!"),
                                        requestField("nickname", JsonFieldType.STRING, "닉네임", false, "한글, 영문, 숫자를 포함하여 2글자 이상 8글자 이하로 입력", "닉네임"),
                                        requestField("terms[].title", JsonFieldType.STRING, "약관 제목", false, "termsOfService, privacyPolicy, ageConfirmation", "termsOfService"),
                                        requestField("terms[].version", JsonFieldType.STRING, "약관 버전", false, "0.1.0 형식", "0.1.0"),
                                        requestField("terms[].agreed", JsonFieldType.BOOLEAN, "약관 동의 여부", false, "boolean 값만 허용됨", "true")
                                ),
                                requestCookies(
                                        cookieWithName("temporary-token").description("임의 엑세스 토큰 쿠키")
                                ),
                                responseFields(
                                        responseField("status", JsonFieldType.NUMBER, "응답 상태 코드", "201"),
                                        responseField("message", JsonFieldType.STRING, "응답 메시지", "정상적으로 일반 회원가입 되었습니다."),
                                        responseField("data.nickname", JsonFieldType.STRING, "응답 데이터", "유저1")
                                )
                ));
    }

    @Test
    void 약관_개수가_올바르지_않을_시_회원가입_실패() throws Exception{
        // given
        String memberInfoJson = """
            {
                "password": "qwer1234!",
                "nickname": "유저1",
                "terms": [
                    {"title": "termsOfService", "version": "0.1.0", "agreed": true},
                    {"title": "privacyPolicy", "version": "0.1.0", "agreed": true}
                ]
            }
        """;

        MockMultipartFile memberInfo = new MockMultipartFile(
                "memberInfo",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                memberInfoJson.getBytes(StandardCharsets.UTF_8)
        );

        given(authService.localSignUp(any(String.class), any(CreateLocalMemberRequest.class), isNull()))
                .willThrow(new BadRequestException("전달된 약관의 수가 올바르지 않습니다."));

        // when
        ResultActions actions = mockMvc.perform(
                multipart("/api/v1/auth/signup/local")
                        .file(memberInfo)
                        .cookie(new Cookie("temporary-token", "temporary-token-value")
        ));

        actions
                .andExpect(content().json("""
                    {
                        "status": 400,
                        "message": "전달된 약관의 수가 올바르지 않습니다.",
                        "errors": []
                    }
                """))
                .andDo(documentHandler.document());
    }

    @Test
    void 존재하지_않는_약관_전달_시_회원가입_실패() throws Exception {
        // given
        String memberInfoJson = """
            {
                "password": "qwer1234!",
                "nickname": "유저1",
                "terms": [
                    {"title": "termsOfService", "version": "0.1.0", "agreed": true},
                    {"title": "privacyPolicy", "version": "0.1.0", "agreed": true},
                    {"title": "nonExistPolicy", "version": "0.1.0", "agreed": true}
                ]
            }
        """;

        MockMultipartFile memberInfo = new MockMultipartFile(
                "memberInfo",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                memberInfoJson.getBytes(StandardCharsets.UTF_8)
        );

        given(authService.localSignUp(any(String.class), any(CreateLocalMemberRequest.class), isNull()))
                .willThrow(new BadRequestException("약관 항목이 일치하지 않습니다."));

        // when
        ResultActions actions = mockMvc.perform(
                multipart("/api/v1/auth/signup/local")
                        .file(memberInfo)
                        .cookie(new Cookie("temporary-token", "temporary-token-value")
                        ));

        actions
                .andExpect(content().json("""
                    {
                        "status": 400,
                        "message": "약관 항목이 일치하지 않습니다.",
                        "errors": []
                    }
                """))
                .andDo(documentHandler.document());
    }

    @Test
    void 필수_약관_미동의_시_회원가입_실패() throws Exception {
        // given
        String memberInfoJson = """
            {
                "password": "qwer1234!",
                "nickname": "유저1",
                "terms": [
                    {"title": "termsOfService", "version": "0.1.0", "agreed": true},
                    {"title": "privacyPolicy", "version": "0.1.0", "agreed": false},
                    {"title": "ageConfirmation", "version": "0.1.0", "agreed": true}
                ]
            }
        """;

        MockMultipartFile memberInfo = new MockMultipartFile(
                "memberInfo",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                memberInfoJson.getBytes(StandardCharsets.UTF_8)
        );

        given(authService.localSignUp(any(String.class), any(CreateLocalMemberRequest.class), isNull()))
                .willThrow(new BadRequestException("필수 약관을 동의하지 않았습니다."));

        // when
        ResultActions actions = mockMvc.perform(
                multipart("/api/v1/auth/signup/local")
                        .file(memberInfo)
                        .cookie(new Cookie("temporary-token", "temporary-token-value")
                        ));

        actions
                .andExpect(content().json("""
                    {
                        "status": 400,
                        "message": "필수 약관을 동의하지 않았습니다.",
                        "errors": []
                    }
                """))
                .andDo(documentHandler.document());
    }

    @Test
    void 최신_버전의_약관이_전달되지_않을_시_회원가입_실패() throws Exception{
        // given
        String memberInfoJson = """
            {
                "password": "qwer1234!",
                "nickname": "유저1",
                "terms": [
                    {"title": "termsOfService", "version": "0.1.0", "agreed": true},
                    {"title": "privacyPolicy", "version": "0.2.0", "agreed": false},
                    {"title": "ageConfirmation", "version": "0.1.0", "agreed": true}
                ]
            }
        """;

        MockMultipartFile memberInfo = new MockMultipartFile(
                "memberInfo",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                memberInfoJson.getBytes(StandardCharsets.UTF_8)
        );

        given(authService.localSignUp(any(String.class), any(CreateLocalMemberRequest.class), isNull()))
                .willThrow(new BadRequestException("최신 버전의 약관이 아닙니다."));

        // when
        ResultActions actions = mockMvc.perform(
                multipart("/api/v1/auth/signup/local")
                        .file(memberInfo)
                        .cookie(new Cookie("temporary-token", "temporary-token-value")
                        ));

        actions
                .andExpect(content().json("""
                    {
                        "status": 400,
                        "message": "최신 버전의 약관이 아닙니다.",
                        "errors": []
                    }
                """))
                .andDo(documentHandler.document());
    }

    @Test
    void 임시_토큰_만료_시_회원가입_실패() throws Exception {
        // given
        String memberInfoJson = """
            {
                "password": "qwer1234!",
                "nickname": "유저1",
                "terms": [
                    {"title": "termsOfService", "version": "0.1.0", "agreed": true},
                    {"title": "privacyPolicy", "version": "0.1.0", "agreed": true},
                    {"title": "ageConfirmation", "version": "0.1.0", "agreed": true}
                ]
            }
        """;

        MockMultipartFile memberInfo = new MockMultipartFile(
                "memberInfo",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                memberInfoJson.getBytes(StandardCharsets.UTF_8)
        );

        given(authService.localSignUp(any(String.class), any(CreateLocalMemberRequest.class), isNull()))
                .willThrow(new UnauthorizedException("토큰이 만료되었습니다."));

        ResultActions actions = mockMvc.perform(
                multipart("/api/v1/auth/signup/local")
                        .file(memberInfo)
                        .cookie(new Cookie("temporary-token", "temporary-token-value")
                        ));

        actions
                .andExpect(content().json("""
                    {
                        "status": 401,
                        "message": "토큰이 만료되었습니다.",
                        "errors": []
                    }
                """))
                .andDo(documentHandler.document());

    }

    @Test
    void 임시_토큰_누락_시_회원가입_실패() throws Exception {
        // given
        String memberInfoJson = """
            {
                "password": "qwer1234!",
                "nickname": "유저1",
                "terms": [
                    {"title": "termsOfService", "version": "0.1.0", "agreed": true},
                    {"title": "privacyPolicy", "version": "0.1.0", "agreed": true},
                    {"title": "ageConfirmation", "version": "0.1.0", "agreed": true}
                ]
            }
        """;

        MockMultipartFile memberInfo = new MockMultipartFile(
                "memberInfo",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                memberInfoJson.getBytes(StandardCharsets.UTF_8)
        );

        given(authService.localSignUp(any(String.class), any(CreateLocalMemberRequest.class), isNull()))
                .willThrow(new BadRequestException("필수 쿠키가 누락되었습니다."));

        ResultActions actions = mockMvc.perform(
                multipart("/api/v1/auth/signup/local")
                        .file(memberInfo));

        actions
                .andExpect(content().json("""
                    {
                                "status": 400,
                                "message": "필수 쿠키가 누락되었습니다.",
                                "errors": [
                                    {
                                        "field": "temporary-token",
                                        "value": null,
                                        "reason": "쿠키가 존재하지 않습니다."
                                    }
                                ]
                            }
                """))
                .andDo(documentHandler.document());

    }

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
