package tosiltosil.backend.module.auth.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tosiltosil.backend.common.config.TestSecurityConfig;
import tosiltosil.backend.common.util.CookieUtil;
import tosiltosil.backend.module.auth.application.AuthService;
import tosiltosil.backend.module.auth.domain.request.LocalLoginRequest;
import tosiltosil.backend.module.auth.domain.response.LocalLoginResponse;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private CookieUtil cookieUtil;

    @Test
    void 정상적인_회원가입_요청_시_201_CREATED_응답_반환() throws Exception {
        // given
        String requestJson = """
            {
                "email": "test@example.com",
                "authNumber": "123456",
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
                "memberInfo.json",
                MediaType.APPLICATION_JSON_VALUE,
                requestJson.getBytes(StandardCharsets.UTF_8)
        );

        // when & then
        mockMvc.perform(multipart("/api/v1/auth/signup/local")
                        .file(memberInfo)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("정상적으로 일반 회원가입 되었습니다."));
    }

    @Test
    void 잘못된_이메일_입력_시_400_예외_반환() throws Exception{
        // given
        String invalidRequestJson = """
            {
                "email": "testexample.com",
                "authNumber": "12346",
                "password": "p",
                "nickname": "n",
                "terms": [
                    {"title": "termsOfService", "version": "0.1.0", "agreed": true},
                    {"title": "privacyPolicy", "version": "0.1.0", "agreed": true},
                    {"title": "ageConfirmation", "version": "0.1.0", "agreed": true}
                ]
            }
        """;

        MockMultipartFile memberInfo = new MockMultipartFile(
                "memberInfo",
                "memberInfo.json",
                MediaType.APPLICATION_JSON_VALUE,
                invalidRequestJson.getBytes(StandardCharsets.UTF_8)
        );

        // when & then
        mockMvc.perform(multipart("/api/v1/auth/signup/local")
                        .file(memberInfo)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors", Matchers.hasSize(4)));
    }

    @Test
    void 정상적인_로그인_시_200_OK_응답_반환() throws Exception {
        // given
        UUID memberId = UUID.randomUUID();
        String accessToken = "access-token";
        String refreshToken = "refresh-token";

        LocalLoginRequest request = new LocalLoginRequest("user@example.com", "qwer1234!");
        LocalLoginResponse response = LocalLoginResponse.of(memberId, accessToken, refreshToken);

        Mockito.when(authService.localLogin(any(LocalLoginRequest.class))).thenReturn(response);
        Mockito.when(cookieUtil.generateAccessAndRefreshTokenCookies(any(), any()))
                .thenReturn(new HttpHeaders() {{
                    add(HttpHeaders.SET_COOKIE, "access-token=token; HttpOnly");
                    add(HttpHeaders.SET_COOKIE, "refresh-token=token; HttpOnly");
                }});

        // when & then
        mockMvc.perform(post("/api/v1/auth/login/local")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("access-token")))
                .andExpect(jsonPath("$.message").value("정상적으로 로그인 되었습니다."))
                .andExpect(jsonPath("$.data.memberId").value(memberId.toString()));
    }

    @Test
    void 이메일_누락_시_400_예외_반환() throws Exception {
        LocalLoginRequest request = new LocalLoginRequest("", "qwer1234!");

        mockMvc.perform(post("/api/v1/auth/login/local")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors[0].reason").value("이메일을 입력해주세요."));
    }

    @Test
    void 비밀번호_형식_오류_시_400_예외_반환() throws Exception {
        LocalLoginRequest request = new LocalLoginRequest("user@example.com", "abcd1234"); // 특수문자 없음

        mockMvc.perform(post("/api/v1/auth/login/local")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors[0].reason").value("비밀번호는 영문, 숫자, 특수문자를 포함하여 8글자 이상으로 입력해주세요."));
    }

    @Test
    void 엑세스_토큰_재발급() throws Exception {
        // given
        String newAccessToken = "access-token";
        String refreshToken = "refresh-token";

        given(authService.reissueAccessToken(refreshToken)).willReturn(newAccessToken);
        given(cookieUtil.generateAccessTokenCookies(newAccessToken)).willReturn(new HttpHeaders());

        // when & then
        mockMvc.perform(get("/api/v1/auth/reissue")
                .cookie(new Cookie("refresh-token", refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("정상적으로 엑세스 토큰을 재발급했습니다."));
    }
}
