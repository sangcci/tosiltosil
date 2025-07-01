package tosiltosil.backend.module.goal.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GoalRepository {

    Optional<Goal> findById(Long goalId);

    List<Goal> findTodayGoalsByMemberId(UUID memberId);

    Goal save(Goal goal);

    List<Goal> saveAll(List<Goal> goals);

    void delete(Goal goal);
}
