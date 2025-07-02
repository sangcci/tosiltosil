package tosiltosil.backend.module.stopwatch.infrastructure;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import tosiltosil.backend.module.stopwatch.domain.Stopwatch;
import tosiltosil.backend.module.stopwatch.domain.StopwatchRepository;

@Repository
@RequiredArgsConstructor
public class StopwatchRepositoryImpl implements StopwatchRepository {

    private final StopwatchJpaRepository stopwatchJpaRepository;

    @Override
    public Optional<Stopwatch> findLatestByGoalId(final Long goalId) {
        return stopwatchJpaRepository.findFirstByGoalIdOrderByCreatedAtDesc(goalId);
    }

    @Override
    public Stopwatch save(final Stopwatch stopwatch) {
        return stopwatchJpaRepository.save(stopwatch);
    }

    @Override
    public void delete(final Stopwatch stopwatch) {
        stopwatchJpaRepository.delete(stopwatch);
    }
}
