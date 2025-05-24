package tosiltosil.backend.common.web.handler;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import tosiltosil.backend.common.domain.CustomException;
import tosiltosil.backend.common.domain.ErrorCode;
import tosiltosil.backend.common.logging.domain.ErrorLog;
import tosiltosil.backend.common.logging.domain.InfoLog;
import tosiltosil.backend.common.web.response.ErrorResponse;
import tosiltosil.backend.common.web.response.ErrorResponse.ErrorDetailResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     *  파라미터 유효성 검증 실패 예외 처리
     *  - javax.validation.Valid 혹은 @Validated 으로 binding error 발생시 발생
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<ErrorDetailResponse> errorDetailResponses = fieldErrors.stream()
                .map(error -> new ErrorDetailResponse(
                        error.getField(),
                        error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                        error.getDefaultMessage()))
                .toList();

        InfoLog infoLog = InfoLog.of("유효하지 않은 파라미터 값");
        infoLog.writeLog();

        return ErrorResponse.of(
                400,
                "파라미터 값이 잘못되었습니다",
                errorDetailResponses.isEmpty() ? null : errorDetailResponses
        );
    }

    /**
     * 바인딩 시 타입 불일치 예외 처리
     * - enum type 일치하지 않아 binding 못할 경우 발생
     * - 주로 @RequestParam enum으로 binding 못했을 경우 발생
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ErrorResponse handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e) {
        ErrorDetailResponse errorDetailResponse = ErrorDetailResponse.of(
                null,
                e.getName(),
                e.getMessage()
        );

        InfoLog infoLog = InfoLog.of("파라미터 타입 바인딩 실패");
        infoLog.writeLog();

        return ErrorResponse.of(
                400,
                "파라미터 타입이 잘못되었습니다",
                List.of(errorDetailResponse)
        );
    }

    /**
     * 바인딩 시 파라미터 누락 예외 처리
     * - 요청 param이 누락되었을 경우 발생
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ErrorResponse handleMissingServletRequestParameterException(final MissingServletRequestParameterException e) {
        ErrorDetailResponse errorDetailResponse = ErrorDetailResponse.of(
                e.getParameterName() + " " + e.getParameterType(),
                null,
                e.getMessage()
        );

        InfoLog infoLog = InfoLog.of("파라미터 누락 바인딩 실패");
        infoLog.writeLog();

        return ErrorResponse.of(
                400,
                "파라미터가 누락되었습니다",
                List.of(errorDetailResponse)
        );
    }

    /**
     * 지원하지 않은 HTTP method 호출 할 경우 발생
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ErrorResponse handleHttpRequestMethodNotSupportedException(final HttpRequestMethodNotSupportedException e) {
        InfoLog infoLog = InfoLog.of("지원하지 않는 HTTP 메서드");
        infoLog.writeLog();

        return ErrorResponse.of(
                405,
                "지원하지 않는 HTTP 메서드입니다",
                List.of(ErrorDetailResponse.of(null, null, e.getMessage()))
        );
    }

    /**
     * 존재하지 않는 api가 호출될 경우 발생
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ErrorResponse handleNoHandlerFoundException(NoHandlerFoundException e) {
        ErrorLog errorLog = ErrorLog.of("지원하지 않는 API 요청", e);
        errorLog.writeLog();

        return ErrorResponse.of(
                404,
                "지원하지 않는 API 요청입니다"
        );
    }

    /**
     * 존재하지 않는 리소스가 호출될 경우 발생
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoResourceFoundException.class)
    public ErrorResponse handleNoResourceFoundException(NoResourceFoundException e) {
        ErrorLog errorLog = ErrorLog.of("지원하지 않는 리소스 요청", e);
        errorLog.writeLog();

        return ErrorResponse.of(
                404,
                "지원하지 않는 리소스 요청입니다"
        );
    }

    /**
     * 비즈니스 요구사항에 따른 예외 처리
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomException.class)
    protected ErrorResponse handleCustomException(final CustomException e) {
        ErrorCode errorCode = e.getErrorCode();

        InfoLog infoLog = InfoLog.of(errorCode.message());
        infoLog.writeLog();

        return ErrorResponse.of(400, errorCode.message());
    }

    /**
     * 서버 내부 예상하지 못한 오류
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    protected ErrorResponse handleException(final Exception e) {
        ErrorLog errorLog = ErrorLog.of("서버 오류", e);
        errorLog.writeLog();

        return ErrorResponse.of(500, "서버 내부에 오류가 발생했습니다");
    }
}
