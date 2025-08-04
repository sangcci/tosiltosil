package tosiltosil.backend.common.web.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;
import tosiltosil.backend.common.web.response.ErrorResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationEntryPointTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException authException;

    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        customAuthenticationEntryPoint = new CustomAuthenticationEntryPoint(objectMapper);
    }

    @Test
    void 인증_실패_시_401_반환() throws IOException, ServletException, IOException {
        // given
        StringWriter stringWriter = prepareResponseWriter();

        ErrorResponse errorResponse = ErrorResponse.of(
                HttpServletResponse.SC_UNAUTHORIZED,
                "인증에 실패했습니다.",
                List.of()
        );

        String expectedJson = objectMapper.writeValueAsString(errorResponse);

        // when
        customAuthenticationEntryPoint.commence(request, response, authException);

        // then
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");

        assertEquals(expectedJson, stringWriter.toString());
    }

    /**
     * HttpServletResponse의 getWriter()가 StringWriter를 반환하도록 설정하여 응답 본문 캡처
     *
     * @return StringWriter
     */
    private StringWriter prepareResponseWriter() throws IOException {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);
        return stringWriter;
    }
}