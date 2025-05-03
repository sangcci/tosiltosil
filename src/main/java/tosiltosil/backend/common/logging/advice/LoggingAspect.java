package tosiltosil.backend.common.logging.advice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    @Pointcut("execution(* tosiltosil.backend..*(..))")
    public void everyRequest() {}

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController) || @within(org.springframework.stereotype.Controller)")
    public void controllerMethods() {}

    @Around("controllerMethods()")
    public Object doLog(final ProceedingJoinPoint joinPoint) throws Throwable {
        String traceId = MDC.get("traceId");
        String userId = MDC.get("userId");
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        // Log entering the method
        log.debug("TraceId: {}, UserId: {}, Class: {}, Entering method {} with parameters {}", traceId, userId, className, methodName, joinPoint.getArgs());
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result;
        try {
            result = joinPoint.proceed();  // Proceed with the method execution
            stopWatch.stop();
            log.debug("TraceId: {}, UserId: {}, Class: {}, Exiting method {} with return value {}, Time: {}ms", traceId, userId, className, methodName, result, stopWatch.getTotalTimeMillis());
            return result;
        } catch (Throwable throwable) {
            stopWatch.stop();
            log.debug("TraceId: {}, UserId: {}, Class: {}, Exception in method {}: {}", traceId, userId, className, methodName, throwable.getMessage());
            throw throwable;
        }
    }
}
