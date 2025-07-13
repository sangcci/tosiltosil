package tosiltosil.backend.module.goal.infrastructure;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import tosiltosil.backend.module.goal.domain.Goal;

public interface GoalJpaRepository extends JpaRepository<Goal, Long> {
    
    List<Goal> findByMemberIdAndCategoryId(UUID memberId, Long categoryId);
}
