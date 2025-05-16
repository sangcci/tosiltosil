package tosiltosil.backend.common.logging.util;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MdcUtils {

    public static void setTraceIdInLog() {
        String traceId = UUID.randomUUID().toString();
        MDC.put("trace_id", traceId);
    }

    public static void setUserIdInLog() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (authentication != null && authentication.isAuthenticated())
                ? authentication.getName()
                : "ANONYMOUS";
        MDC.put("user_id", userId);
    }
}
