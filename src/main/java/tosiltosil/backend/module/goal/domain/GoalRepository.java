package tosiltosil.backend.module.goal.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GoalRepository {

    Optional<Goal> findById(Long goalId);

    List<Goal> findGoalsByMemberIdAndDate(UUID memberId, LocalDate date);

    List<Goal> findByMemberIdAndCategoryId(UUID memberId, Long categoryId);

    List<Goal> findByMemberIdAndCategoryIdAndDateFromToday(UUID memberId, Long categoryId);

    Goal save(Goal goal);

    List<Goal> saveAll(List<Goal> goals);

    void delete(Goal goal);
}
