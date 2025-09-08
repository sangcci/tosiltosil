package tosiltosil.backend.module.progress.domain;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

public interface ProgressRepository {

    Optional<Progress> findByMemberId(UUID memberId);

    void save(Progress progress);

    Duration findTodayDurationByMemberId(UUID memberId);

    void saveTodayDuration(UUID memberId, Duration duration);
}
