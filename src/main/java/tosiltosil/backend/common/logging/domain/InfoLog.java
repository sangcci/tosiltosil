package tosiltosil.backend.common.logging.domain;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InfoLog extends Log {

    private final String message;

    public InfoLog(final String message) {
        this.message = message;
    }

    @Override
    public void writeLog() {
        log.info(getLogMessage());
    }
}
