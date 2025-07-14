package tosiltosil.backend.module.goal.infrastructure;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import tosiltosil.backend.module.goal.domain.Goal;

public interface GoalJpaRepository extends JpaRepository<Goal, Long> {
    
    List<Goal> findByMemberIdAndCategoryId(UUID memberId, Long categoryId);

    List<Goal> findByCategoryIdAndDateOrderByOrderKey(Long categoryId, LocalDate date);

    List<Goal> findByMemberIdAndDateOrderByOrderKey(UUID memberId, LocalDate date);
}
