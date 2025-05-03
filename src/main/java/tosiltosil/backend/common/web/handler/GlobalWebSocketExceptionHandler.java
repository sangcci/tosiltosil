package tosiltosil.backend.common.web.handler;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import tosiltosil.backend.common.domain.CustomException;
import tosiltosil.backend.common.domain.ErrorCode;

@Slf4j
@ControllerAdvice
public class GlobalWebSocketExceptionHandler {

    @MessageExceptionHandler(Exception.class)
    @SendToUser("/queue/errors")
    public ErrorResponse handleException(final Exception exception) {
        log.error("Unexpected error occurred. exception: {}", exception.getMessage());
        return new ErrorResponse("TOSSIL01", "INTERNAL_SERVER_EXCEPTION", "Unexpected error occurred");
    }

    @MessageExceptionHandler(CustomException.class)
    @SendToUser("/queue/errors")
    public ErrorResponse handleStompException(final CustomException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        log.info("The wrong request came in. message: {}, exception: {}",
                errorCode.message(),
                exception.getMessage()
        );
        return new ErrorResponse(errorCode.customCode(), errorCode.title(), errorCode.message());
    }

    @MessageExceptionHandler(MethodArgumentNotValidException.class)
    @SendToUser("/queue/errors")
    public ErrorResponse handleValidationExceptions(final MethodArgumentNotValidException exception) {
        List<String> errors = exception.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        log.info("The wrong param came in. message: {}", errors);
        return new ErrorResponse("TOSSIL02", "PARAM_EXCEPTION", errors.get(0));
    }
}
