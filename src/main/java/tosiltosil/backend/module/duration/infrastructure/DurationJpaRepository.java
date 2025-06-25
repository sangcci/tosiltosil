package tosiltosil.backend.module.duration.infrastructure;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import tosiltosil.backend.module.duration.domain.DailyDuration;

public interface DurationJpaRepository extends JpaRepository<DailyDuration, Long> {

    DailyDuration findByMemberId(UUID memberId);
}
