package tosiltosil.backend.module.goal.infrastructure;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import tosiltosil.backend.common.domain.holder.TimeHolder;
import tosiltosil.backend.module.goal.domain.Goal;
import tosiltosil.backend.module.goal.domain.GoalRepository;
import tosiltosil.backend.module.goal.domain.response.GoalListPerCategoryResponse;

@Repository
@RequiredArgsConstructor
public class GoalRepositoryImpl implements GoalRepository {

    private final GoalJpaRepository goalJpaRepository;
    private final GoalDslRepository goalDslRepository;
    private final TimeHolder timeHolder;

    @Override
    public Optional<Goal> findById(final Long goalId) {
        return goalJpaRepository.findById(goalId);
    }

    @Override
    public List<GoalListPerCategoryResponse> findDayGoals(final UUID memberId, final LocalDate date) {
        return goalDslRepository.findDayGoals(memberId, date);
    }

    @Override
    public List<Goal> findTotalGoals(final UUID memberId, final Long categoryId) {
        return goalJpaRepository.findByMemberIdAndCategoryId(memberId, categoryId);
    }

    @Override
    public List<Goal> findTodayGoals(final UUID memberId) {
        return goalJpaRepository.findByMemberIdAndDate(memberId, timeHolder.getCurrentDate());
    }

    @Override
    public List<Goal> findTodayGoalsInCategory(final UUID memberId, final Long categoryId) {
        return goalJpaRepository.findByMemberIdAndCategoryIdAndDateOrderByOrderIndexAsc(memberId, categoryId, timeHolder.getCurrentDate());
    }

    @Override
    public Optional<BigDecimal> findLastOrderIndex(final UUID memberId) {
        return goalJpaRepository.findMaxOrderIndexByMemberIdAndDate(memberId, timeHolder.getCurrentDate());
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

    @Override
    public void deleteAllByMemberIdAndCategoryId(UUID memberId, Long categoryId) {
        goalJpaRepository.deleteAllByMemberIdAndCategoryId(memberId, categoryId);
    }
}
