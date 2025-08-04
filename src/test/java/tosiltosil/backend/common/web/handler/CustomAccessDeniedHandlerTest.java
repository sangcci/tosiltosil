package tosiltosil.backend.common.web.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import tosiltosil.backend.common.web.response.ErrorResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomAccessDeniedHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private AccessDeniedException accessDeniedException;

    private CustomAccessDeniedHandler customAccessDeniedHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        customAccessDeniedHandler = new CustomAccessDeniedHandler(objectMapper);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        // 테스트 후 SecurityContextHolder를 초기화하여 테스트 간 격리 보장
        SecurityContextHolder.clearContext();
    }

    @Test
    void 인증되지_않은_사용자가_접근_시_401_반환() throws IOException, ServletException {
        // given
        // 익명 사용자(AnonymousAuthenticationToken) 설정
        Authentication anonymousAuth = new AnonymousAuthenticationToken("key", "anonymousUser",
                AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));

        when(securityContext.getAuthentication()).thenReturn(anonymousAuth);

        StringWriter stringWriter = prepareResponseWriter();

        ErrorResponse errorResponse = ErrorResponse.of(
                HttpServletResponse.SC_UNAUTHORIZED,
                "인증에 실패했습니다.",
                Collections.emptyList()
        );

        String expectedJson = objectMapper.writeValueAsString(errorResponse);

        // when
        customAccessDeniedHandler.handle(request, response, accessDeniedException);

        // then
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
        verify(response).setCharacterEncoding("UTF-8");

        assertEquals(expectedJson, stringWriter.toString());
    }

    @Test
    void 권한이_없는_사용자가_접근_시_403_반환() throws IOException, ServletException {
        // given
        // 인증된 사용자(UsernamePasswordAuthenticationToken) 설정
        Authentication authenticatedAuth = new UsernamePasswordAuthenticationToken("user", "password",
                AuthorityUtils.createAuthorityList("ROLE_USER"));
        when(securityContext.getAuthentication()).thenReturn(authenticatedAuth);

        StringWriter stringWriter = prepareResponseWriter();

        ErrorResponse errorResponse = ErrorResponse.of(
                HttpServletResponse.SC_FORBIDDEN,
                "요청에 대한 권한이 없습니다.",
                List.of()
        );

        String expectedJson = objectMapper.writeValueAsString(errorResponse);

        // when
        customAccessDeniedHandler.handle(request, response, accessDeniedException);

        // then
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
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