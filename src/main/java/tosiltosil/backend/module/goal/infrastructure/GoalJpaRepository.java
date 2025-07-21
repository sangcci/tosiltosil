package tosiltosil.backend.module.goal.infrastructure;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tosiltosil.backend.module.goal.domain.Goal;

public interface GoalJpaRepository extends JpaRepository<Goal, Long> {

    List<Goal> findByMemberIdAndCategoryId(UUID memberId, Long categoryId);

    List<Goal> findByMemberIdAndCategoryIdAndDateOrderByOrderIndexAsc(UUID memberId, Long categoryId, LocalDate date);

    @Query("SELECT MAX(g.orderIndex) FROM Goal g WHERE g.memberId = :memberId AND g.date = :date")
    Optional<BigDecimal> findMaxOrderIndexByMemberIdAndDate(@Param("memberId") UUID memberId, @Param("date") LocalDate date);
}
