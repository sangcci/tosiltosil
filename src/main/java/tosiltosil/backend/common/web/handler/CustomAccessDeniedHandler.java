package tosiltosil.backend.common.web.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tosiltosil.backend.common.logging.domain.InfoLog;
import tosiltosil.backend.common.web.response.ErrorResponse;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        int statusCode;
        String errorMessage;

        if (authentication instanceof AnonymousAuthenticationToken) {
            statusCode = HttpServletResponse.SC_UNAUTHORIZED;
            errorMessage = "인증에 실패했습니다.";
        } else {
            statusCode = HttpServletResponse.SC_FORBIDDEN;
            errorMessage = "요청에 대한 권한이 없습니다.";
        }

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
