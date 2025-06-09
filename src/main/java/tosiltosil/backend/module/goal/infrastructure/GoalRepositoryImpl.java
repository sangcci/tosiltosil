package tosiltosil.backend.module.goal.infrastructure;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import tosiltosil.backend.module.goal.domain.Goal;
import tosiltosil.backend.module.goal.domain.GoalRepository;

@Repository
@RequiredArgsConstructor
public class GoalRepositoryImpl implements GoalRepository {

    private final GoalJpaRepository goalJpaRepository;

    @Override
    public Optional<Goal> findById(final Long goalId) {
        return goalJpaRepository.findById(goalId);
    }

    @Override
    public Optional<Goal> findByIdAndMemberId(final Long goalId, final UUID memberId) {
        return goalJpaRepository.findByIdAndMemberId(goalId, memberId);
    }

    @Override
    public Goal save(final Goal goal) {
        return goalJpaRepository.save(goal);
    }

    @Override
    public void saveAll(final List<Goal> goals) {
        // TODO: bulk insert 구현
        goalJpaRepository.saveAll(goals);
    }

    @Override
    public void delete(final Goal goal) {
        goalJpaRepository.delete(goal);
    }
}
