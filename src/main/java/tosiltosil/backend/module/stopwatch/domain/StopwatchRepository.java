package tosiltosil.backend.module.stopwatch.domain;

import java.util.Optional;
import java.util.UUID;

public interface StopwatchRepository {

    // stopwatch
    Optional<Stopwatch> findLatestByGoalId(Long goalId);

    Stopwatch save(Stopwatch stopwatch);

    void delete(Stopwatch stopwatch);

    // stopwatch activity
    Optional<StopwatchActivity> findStopwatchActivityByMemberId(UUID memberId);

    void saveStopwatchActivity(StopwatchActivity stopwatchActivity);
}
