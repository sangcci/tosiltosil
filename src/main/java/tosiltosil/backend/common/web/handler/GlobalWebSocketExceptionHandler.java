package tosiltosil.backend.common.web.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;
import tosiltosil.backend.common.domain.exception.CustomException;
import tosiltosil.backend.common.domain.exception.ErrorCode;
import tosiltosil.backend.common.logging.domain.ErrorLog;
import tosiltosil.backend.common.logging.domain.InfoLog;
import tosiltosil.backend.common.web.response.ErrorResponse;

@Slf4j
@ControllerAdvice
public class GlobalWebSocketExceptionHandler {

    @MessageExceptionHandler(CustomException.class)
    @SendToUser("/queue/errors")
    public ErrorResponse handleCustomException(final CustomException e) {
        ErrorCode errorCode = e.getErrorCode();

        InfoLog infoLog = InfoLog.of(errorCode.message());
        infoLog.writeLog();

        return ErrorResponse.of(400, errorCode.message());
    }

    @MessageExceptionHandler(Exception.class)
    @SendToUser("/queue/errors")
    public ErrorResponse handleException(final Exception e) {
        ErrorLog errorLog = ErrorLog.of("서버 오류", e);
        errorLog.writeLog();

        return ErrorResponse.of(500, "서버 내부에 오류가 발생했습니다");
    }
}
