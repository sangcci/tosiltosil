package tosiltosil.backend.module.goal.infrastructure;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import tosiltosil.backend.module.goal.domain.DailyTotalTime;

public interface DailyTotalTimeJpaRepository extends JpaRepository<DailyTotalTime, Long> {

    DailyTotalTime findByMemberId(UUID memberId);
}
