package tosiltosil.backend.common.logging.domain;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorLog extends Log {

    private final String message;
    private final String exceptionName;
    private final String exceptionMessage;
    private final String stackTrace;

    public static ErrorLog of(
            final String message,
            final Throwable throwable
    ) {
        String exceptionName = throwable.getClass().getName();
        String exceptionMessage = throwable.getMessage();
        String stackTrace = Arrays.toString(throwable.getStackTrace());
        return new ErrorLog(message, exceptionName, exceptionMessage, stackTrace);
    }

    @Override
    public void writeLog() {
        log.error(getLogMessage());
    }
}
