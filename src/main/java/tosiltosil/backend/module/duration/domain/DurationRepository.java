package tosiltosil.backend.module.duration.domain;

import java.time.Duration;
import java.util.UUID;

public interface DurationRepository {

    Duration findTodayDuration(UUID memberId);

    void saveTodayDuration(UUID memberId, Duration duration);
}
