package tosiltosil.backend.module.auth.presentation;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tosiltosil.backend.common.config.TestSecurityConfig;
import tosiltosil.backend.module.auth.application.AuthService;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void 회원가입_정상_요청() throws Exception {
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
    void 회원가입_유효성_검증_실패() throws Exception{
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
}
