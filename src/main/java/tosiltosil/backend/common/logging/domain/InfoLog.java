package tosiltosil.backend.common.logging.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class InfoLog extends Log {

    private final String message;

    public static InfoLog of(final String message) {
        return new InfoLog(message);
    }

    @Override
    public void writeLog() {
        log.info(message);
    }
}
