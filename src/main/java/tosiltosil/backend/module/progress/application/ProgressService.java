package tosiltosil.backend.module.progress.application;

import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tosiltosil.backend.module.progress.infrastructure.ProgressRedisRepository;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final ProgressRedisRepository progressRedisRepository;

    public Duration getTodayDuration(final UUID memberId) {
        return progressRedisRepository.findTodayDuration(memberId);
    }

    public Duration updateTodayDuration(final UUID memberId, final Duration duration) {
        Duration todayTotalTime = progressRedisRepository.findTodayDuration(memberId);
        todayTotalTime = todayTotalTime.plus(duration);
        progressRedisRepository.cacheTodayDuration(memberId, todayTotalTime);
        return todayTotalTime;
    }
    
    public Duration subtractTodayDuration(final UUID memberId, final Duration duration) {
        Duration todayTotalTime = progressRedisRepository.findTodayDuration(memberId);
        todayTotalTime = todayTotalTime.minus(duration);
        if (todayTotalTime.isNegative()) {
            todayTotalTime = Duration.ZERO;
        }
        progressRedisRepository.cacheTodayDuration(memberId, todayTotalTime);
        return todayTotalTime;
    }
}
