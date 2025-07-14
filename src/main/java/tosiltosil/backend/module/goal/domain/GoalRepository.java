package tosiltosil.backend.module.goal.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import tosiltosil.backend.module.goal.domain.response.DayGoalListResponse;

public interface GoalRepository {

    Optional<Goal> findById(Long goalId);

    List<DayGoalListResponse> findDayGoals(UUID memberId, LocalDate date);

    List<Goal> findGoal(UUID memberId, Long categoryId);

    Goal save(Goal goal);

    List<Goal> saveAll(List<Goal> goals);

    void delete(Goal goal);

    List<Goal> findByCategoryIdAndDateOrderByOrderKey(Long categoryId, LocalDate date);

    List<Goal> findByMemberIdAndDateOrderByOrderKey(UUID memberId, LocalDate date);
}
