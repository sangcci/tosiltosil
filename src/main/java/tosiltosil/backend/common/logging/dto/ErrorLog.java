package tosiltosil.backend.common.logging.dto;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ErrorLog extends Log {

    private final String message;
    private final String exceptionName;
    private final String exceptionMessage;
    private final String stackTrace;

    public ErrorLog(
            final String message,
            final Throwable throwable
    ) {
        this.message = message;
        this.exceptionName = throwable.getClass().getName();
        this.exceptionMessage = throwable.getMessage();
        this.stackTrace = Arrays.toString(throwable.getStackTrace());
    }

    @Override
    public void writeLog() {
        log.error(getLogMessage());
    }
}
