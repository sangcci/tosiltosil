package tosiltosil.backend.module.goal.application;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tosiltosil.backend.module.goal.domain.Goal;
import tosiltosil.backend.module.goal.domain.GoalRepository;
import tosiltosil.backend.common.domain.holder.TimeHolder;

@Service
@RequiredArgsConstructor
public class CategoryGoalService {

    private final GoalRepository goalRepository;
    private final TimeHolder timeHolder;

    @Transactional
    public Duration deleteGoalsAndCalculateTotalDuration(final UUID memberId, final Long categoryId) {
        List<Goal> goalsToDelete = goalRepository.findByMemberIdAndCategoryId(memberId, categoryId);

        Duration totalDuration = goalsToDelete.stream()
                .map(Goal::getDuration)
                .reduce(Duration.ZERO, Duration::plus);

        goalsToDelete.forEach(goalRepository::delete);

        return totalDuration;
    }

    @Transactional
    public void updateGoalsCategoryFromToday(final UUID memberId, final Long oldCategoryId, final Long newCategoryId) {
        LocalDate today = timeHolder.getCurrentDate();
        
        // 오늘 이후 목표에 카테고리 업데이트
        List<Goal> goalsToUpdate = goalRepository.findByMemberIdAndCategoryIdAndDateFromToday(memberId, oldCategoryId);
        
        for (Goal goal : goalsToUpdate) {
            goal.updateBasicInfo(goal.getTitle(), newCategoryId, goal.getIconId());
        }
        
        if (!goalsToUpdate.isEmpty()) {
            goalRepository.saveAll(goalsToUpdate);
        }
    }
}