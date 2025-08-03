package tosiltosil.backend.module.auth.presentation;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;
import tosiltosil.backend.common.domain.exception.BadRequestException;
import tosiltosil.backend.common.domain.exception.UnauthorizedException;
import tosiltosil.backend.common.util.CookieUtil;
import tosiltosil.backend.module.auth.application.AuthService;
import tosiltosil.backend.module.auth.domain.request.CreateLocalMemberRequest;
import tosiltosil.backend.module.auth.domain.response.CreateLocalMemberResponse;
import tosiltosil.backend.support.RestDocsTestSupport;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@SuppressWarnings("NonAsciiCharacters")
public class SignUpControllerRestDocsTest extends RestDocsTestSupport {

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private CookieUtil cookieUtil;

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
                                        requestCookie("temporary-token", "임의 엑세스 토큰 쿠키")
                                ),
                                responseFields(
                                        responseField("status", JsonFieldType.NUMBER, "응답 상태 코드", "201"),
                                        responseField("message", JsonFieldType.STRING, "응답 메시지", "정상적으로 일반 회원가입 되었습니다."),
                                        responseField("data", JsonFieldType.OBJECT, "응답 데이터", "{}"),
                                        responseField("data.nickname", JsonFieldType.STRING, "사용자 닉네임", "유저1")
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
}
