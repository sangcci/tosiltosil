package tosiltosil.backend.module.goal.application;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tosiltosil.backend.module.goal.domain.Goal;
import tosiltosil.backend.module.goal.domain.GoalRepository;

@Service
@RequiredArgsConstructor
public class CategoryGoalService {

    private final GoalRepository goalRepository;

    @Transactional
    public Duration deleteGoalsAndCalculateTotalDuration(final UUID memberId, final Long categoryId) {
        List<Goal> goalsToDelete = goalRepository.findByMemberIdAndCategoryId(memberId, categoryId);

        Duration totalDuration = goalsToDelete.stream()
                .map(Goal::getDuration)
                .reduce(Duration.ZERO, Duration::plus);

        goalsToDelete.forEach(goalRepository::delete);

        return totalDuration;
    }
}