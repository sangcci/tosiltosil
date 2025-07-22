package tosiltosil.backend.module.goal.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import tosiltosil.backend.module.goal.domain.response.GoalListPerCategoryResponse;

public interface GoalRepository {

    Optional<Goal> findById(Long goalId);

    List<GoalListPerCategoryResponse> findDayGoals(UUID memberId, LocalDate date);

    List<Goal> findTotalGoals(UUID memberId, Long categoryId);

    List<Goal> findTodayGoals(UUID memberId);

    List<Goal> findTodayGoalsInCategory(UUID memberId, Long categoryId);

    Optional<BigDecimal> findLastOrderIndex(UUID memberId);

    Goal save(Goal goal);

    List<Goal> saveAll(List<Goal> goals);

    void delete(Goal goal);
}
