package tosiltosil.backend.module.goal.infrastructure;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tosiltosil.backend.module.goal.domain.Goal;

public interface GoalJpaRepository extends JpaRepository<Goal, Long> {
    
    List<Goal> findByMemberIdAndCategoryIdOrderByOrderIndexAsc(UUID memberId, Long categoryId);

    @Query("SELECT MAX(g.orderIndex) FROM Goal g WHERE g.memberId = :memberId")
    Optional<BigDecimal> findMaxOrderIndexByMemberId(@Param("memberId") UUID memberId);
}
