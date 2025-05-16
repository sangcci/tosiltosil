package tosiltosil.backend.common.logging.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Log {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(Log.class);

    protected String getLogMessage() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.error("로그 데이터 직렬화에 실패했습니다.", e);
            return "{}";
        }
    }

    public abstract void writeLog();
}
