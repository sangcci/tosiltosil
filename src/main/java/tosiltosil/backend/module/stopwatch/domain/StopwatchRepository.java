package tosiltosil.backend.module.stopwatch.domain;

import java.util.Optional;

public interface StopwatchRepository {

    Optional<Stopwatch> findLatestByGoalId(Long goalId);

    Stopwatch save(Stopwatch stopwatch);

    void delete(Stopwatch stopwatch);
}
