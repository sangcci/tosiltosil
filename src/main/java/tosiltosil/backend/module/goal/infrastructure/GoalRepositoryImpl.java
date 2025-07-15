package tosiltosil.backend.module.goal.infrastructure;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import tosiltosil.backend.module.goal.domain.Goal;
import tosiltosil.backend.module.goal.domain.GoalRepository;
import tosiltosil.backend.module.goal.domain.response.DayGoalListResponse;

@Repository
@RequiredArgsConstructor
public class GoalRepositoryImpl implements GoalRepository {

    private final GoalJpaRepository goalJpaRepository;
    private final GoalDslRepository goalDslRepository;

    @Override
    public Optional<Goal> findById(final Long goalId) {
        return goalJpaRepository.findById(goalId);
    }

    @Override
    public List<DayGoalListResponse> findDayGoals(final UUID memberId, final LocalDate date) {
        return goalDslRepository.findDayGoals(memberId, date);
    }

    @Override
    public List<Goal> findGoal(final UUID memberId, final Long categoryId) {
        return goalJpaRepository.findByMemberIdAndCategoryIdOrderByOrderIndexAsc(memberId, categoryId);
    }

    @Override
    public Goal save(final Goal goal) {
        return goalJpaRepository.save(goal);
    }

    @Override
    public List<Goal> saveAll(final List<Goal> goals) {
        // TODO: bulk insert 구현
        return goalJpaRepository.saveAll(goals);
    }

    @Override
    public void delete(final Goal goal) {
        goalJpaRepository.delete(goal);
    }
}
