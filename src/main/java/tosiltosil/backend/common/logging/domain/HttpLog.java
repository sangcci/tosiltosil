package tosiltosil.backend.common.logging.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Getter
@RequiredArgsConstructor
public class HttpLog extends Log {

    private final String requestMethod;
    private final String requestUri;
    private final int responseStatus;
    private final Long duration;

    public static HttpLog of(
            final ContentCachingRequestWrapper request,
            final ContentCachingResponseWrapper response,
            final Long duration
    ) {
        // Request URI 설정
        String method = request.getMethod();
        String uri = request.getRequestURI();
        int status = response.getStatus();
        return new HttpLog(method, uri, status, duration);
    }

    @Override
    public void writeLog() {
        log.info(getLogMessage());
    }
}
