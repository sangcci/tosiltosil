package tosiltosil.backend.common.web.handler;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tosiltosil.backend.common.domain.CustomException;
import tosiltosil.backend.common.domain.ErrorCode;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(final Exception exception, final HttpServletRequest request) {
        log.error("Unexpected error occurred. uri: {} {}, exception: {}",
                request.getMethod(),
                request.getRequestURI(),
                exception.getMessage()
        );
        return new ResponseEntity<>(
                new ErrorResponse("TOSSIL01", "INTERNAL_SERVER_EXCEPTION", "Unexpected error occurred"),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(final CustomException exception, final HttpServletRequest request) {
        ErrorCode errorCode = exception.getErrorCode();
        log.info("The wrong request came in. uri: {} {}, message: {}, exception: {}",
                request.getMethod(),
                request.getRequestURI(),
                errorCode.message(),
                exception.getMessage()
        );
        return new ResponseEntity<>(
                new ErrorResponse(errorCode.customCode(), errorCode.title(), errorCode.message()),
                HttpStatusCode.valueOf(errorCode.httpStatus())
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(final MethodArgumentNotValidException exception, final HttpServletRequest request) {
        List<String> errors = exception.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();
        log.info("The wrong param came in. uri: {} {}, message: {}",
                request.getMethod(),
                request.getRequestURI(),
                errors
        );
        return new ResponseEntity<>(
                new ErrorResponse("TOSSIL02", "PARAM_EXCEPTION", errors.get(0)),
                HttpStatus.BAD_REQUEST
        );
    }
}
