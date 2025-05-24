package tosiltosil.backend.common.logging.domain;

import static net.logstash.logback.argument.StructuredArguments.kv;

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
        String method = request.getMethod();
        String uri = request.getRequestURI();
        int status = response.getStatus();
        return new HttpLog(method, uri, status, duration);
    }

    @Override
    public void writeLog() {
        log.info("HTTP 로그",
                kv("requestMethod", requestMethod),
                kv("requestUri", requestUri),
                kv("responseStatus", responseStatus),
                kv("duration", duration));
    }
}
