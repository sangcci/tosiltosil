package tosiltosil.backend.common.logging.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class TracingFilter extends HttpFilter {

    private static final String HEADER_TRACE_ID = "X-Trace-Id";

    @Override
    protected void doFilter(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain
    ) throws ServletException, IOException {
        // Get traceId from the request header, or generate a new one if missing
        String traceId = request.getHeader(HEADER_TRACE_ID);
        if (traceId == null || traceId.isEmpty()) {
            traceId = UUID.randomUUID().toString();
        }
        MDC.put("traceId", traceId);
        MDC.put("userId", "ANONYMOUS"); // Default userId
        MDC.put("ipAddress", request.getRemoteAddr());
        MDC.put("requestUri", request.getRequestURI());
        MDC.put("requestMethod", request.getMethod());

        // Retrieve userId from the security context
        /*
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (authentication != null && authentication.isAuthenticated()) ? authentication.getName() : "ANONYMOUS";
        MDC.put(USER_ID, userId);
        */

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
