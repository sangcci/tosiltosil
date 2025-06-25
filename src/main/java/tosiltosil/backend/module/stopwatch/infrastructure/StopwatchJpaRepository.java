package tosiltosil.backend.module.stopwatch.infrastructure;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import tosiltosil.backend.module.stopwatch.domain.Stopwatch;

public interface StopwatchJpaRepository extends JpaRepository<Stopwatch, Long> {

    Optional<Stopwatch> findFirstByGoalIdOrderByCreatedAtDesc(Long goalId);
}
