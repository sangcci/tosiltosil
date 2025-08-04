package tosiltosil.backend.common.web.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tosiltosil.backend.common.logging.domain.InfoLog;
import tosiltosil.backend.common.web.response.ErrorResponse;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        int statusCode = HttpServletResponse.SC_UNAUTHORIZED;
        String errorMessage = "인증에 실패했습니다.";

        ErrorResponse errorResponse = ErrorResponse.of(
                statusCode,
                errorMessage,
                List.of()
        );

        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        InfoLog.of(errorMessage).writeLog();

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
