package tosiltosil.backend.module.duration.application;

import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tosiltosil.backend.module.duration.domain.DurationRepository;

@Service
@RequiredArgsConstructor
public class DurationService {

    private final DurationRepository durationRepository;

    public Duration getTodayDuration(final UUID memberId) {
        return durationRepository.findTodayDuration(memberId);
    }

    public Duration updateTodayDuration(final UUID memberId, final Duration duration) {
        Duration todayTotalTime = durationRepository.findTodayDuration(memberId);
        todayTotalTime = todayTotalTime.plus(duration);
        durationRepository.saveTodayDuration(memberId, todayTotalTime);
        return todayTotalTime;
    }
}
