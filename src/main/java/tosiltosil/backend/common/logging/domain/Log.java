package tosiltosil.backend.common.logging.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Log {

    protected static final Logger log = LoggerFactory.getLogger(Log.class);

    public abstract void writeLog();
}
