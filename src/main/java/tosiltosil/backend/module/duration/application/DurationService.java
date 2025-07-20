package tosiltosil.backend.module.duration.application;

import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tosiltosil.backend.module.duration.infrastructure.DurationRedisRepository;

@Service
@RequiredArgsConstructor
public class DurationService {

    private final DurationRedisRepository durationRedisRepository;

    public Duration getTodayDuration(final UUID memberId) {
        return durationRedisRepository.findTodayDuration(memberId);
    }

    public Duration updateTodayDuration(final UUID memberId, final Duration duration) {
        Duration todayTotalTime = durationRedisRepository.findTodayDuration(memberId);
        todayTotalTime = todayTotalTime.plus(duration);
        durationRedisRepository.cacheTodayDuration(memberId, todayTotalTime);
        return todayTotalTime;
    }
    
    public Duration subtractTodayDuration(final UUID memberId, final Duration duration) {
        Duration todayTotalTime = durationRedisRepository.findTodayDuration(memberId);
        todayTotalTime = todayTotalTime.minus(duration);
        if (todayTotalTime.isNegative()) {
            todayTotalTime = Duration.ZERO;
        }
        durationRedisRepository.cacheTodayDuration(memberId, todayTotalTime);
        return todayTotalTime;
    }
}
