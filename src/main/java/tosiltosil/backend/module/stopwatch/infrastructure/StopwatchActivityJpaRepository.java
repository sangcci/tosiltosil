package tosiltosil.backend.module.stopwatch.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import tosiltosil.backend.module.stopwatch.domain.StopwatchActivity;

import java.util.Optional;
import java.util.UUID;

public interface StopwatchActivityJpaRepository extends JpaRepository<StopwatchActivity, Long> {

    Optional<StopwatchActivity> findByMemberId(UUID memberId);
}
