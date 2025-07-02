package tosiltosil.backend.module;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;
import tosiltosil.backend.common.logging.domain.ErrorLog;
import tosiltosil.backend.common.logging.domain.InfoLog;

@Component
@RequiredArgsConstructor
public class RedisHealthChecker {

    private final RedisConnectionFactory redisConnectionFactory;

    @EventListener(ApplicationReadyEvent.class)
    public void checkRedisConnectionOnStartup() {
        InfoLog infoLog = InfoLog.of("Redis 연결 상태 최종 확인");
        infoLog.writeLog();

        checkRedisConnection();
    }

    public boolean checkRedisConnection() {
        RedisConnection connection = null;
        try {
            connection = redisConnectionFactory.getConnection();
            String pingResult = connection.ping();

            InfoLog infoLog = InfoLog.of("Redis 서버 정상 동작 확인 (응답: " + pingResult + ")");
            infoLog.writeLog();

            return "PONG".equals(pingResult);

        } catch (Exception e) {
            ErrorLog errorLog = ErrorLog.of("Redis 연결 실패", e);
            errorLog.writeLog();
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    ErrorLog errorLog = ErrorLog.of("Redis 연결 정리 중 오류 발생", e);
                    errorLog.writeLog();
                }
            }
        }
    }

    public void printRedisStatus() {
        boolean isConnected = checkRedisConnection();
        if (isConnected) {
            InfoLog infoLog = InfoLog.of("Redis 상태: 정상");
            infoLog.writeLog();
        } else {
            InfoLog infoLog = InfoLog.of("Redis 상태: 연결 불가");
            infoLog.writeLog();
        }
    }
}