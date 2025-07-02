package tosiltosil.backend.module.duration.domain;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

public interface DurationRepository {

    Optional<DailyDuration> findByMemberId(UUID memberId);

    Duration findTodayDuration(UUID memberId);

    void save(DailyDuration dailyDuration);

    void saveTodayDuration(UUID memberId, Duration duration);
}
