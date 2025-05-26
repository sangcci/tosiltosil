package tosiltosil.backend.common.logging.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StopWatch;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import tosiltosil.backend.common.logging.domain.HttpLog;
import tosiltosil.backend.common.logging.util.MdcUtils;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HttpLogFilter extends OncePerRequestFilter {

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    private static final ArrayList<String> loggingExcludeUrls = new ArrayList<>(
            List.of("/actuator/**", "/swagger/**", "/swagger-ui/**", "/api-docs/**", "/favicon.ico")
    );

    /**
     * 모니터링 및 스웨거 요청은 로깅에서 제외합니다.
     * @return 모니터링 요청 URI 여부
     */
    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) throws ServletException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);

        return loggingExcludeUrls.stream().anyMatch(
                uri -> pathMatcher.match(uri, requestWrapper.getRequestURI())
        );
    }

    /**
     * HTTP 요청 및 응답 로깅을 위한 필터입니다.
     */
    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain
    ) throws ServletException, IOException {
        // 요청 응답 본문 캐싱
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        // 로깅 추적을 위한 Trace ID 설정
        MdcUtils.setTraceIdInLog();

        // 유저 아이디 설정
        MdcUtils.setUserIdInLog();

        // 실행 시간 설정
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        filterChain.doFilter(requestWrapper, responseWrapper);
        stopWatch.stop();
        Long duration = stopWatch.getTotalTimeMillis();

        // 로그 남기기
        HttpLog httpLog = HttpLog.of(requestWrapper, responseWrapper, duration);
        httpLog.writeLog();

        // 실제 응답에 Body 복사
        responseWrapper.copyBodyToResponse();
    }
}
