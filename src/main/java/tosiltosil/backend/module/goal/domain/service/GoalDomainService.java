package tosiltosil.backend.module.goal.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tosiltosil.backend.common.domain.exception.BadRequestException;
import tosiltosil.backend.common.domain.holder.TimeHolder;
import tosiltosil.backend.module.goal.domain.Goal;
import tosiltosil.backend.module.goal.domain.GoalRepository;
import tosiltosil.backend.module.goal.domain.value.GoalStatus;

@Component
@RequiredArgsConstructor
public class GoalDomainService {

    private final GoalRepository goalRepository;
    private final TimeHolder timeHolder;

    public void validateGoalDate(final LocalDate date) {
        LocalDate today = timeHolder.getCurrentDate();
        if (date.isBefore(today)) {
            throw new BadRequestException("날짜는 오늘 이후여야 합니다.");
        }
    }

    public BigDecimal calculateGoalAchievedPercentage(final UUID memberId) {
        List<Goal> todayGoals = goalRepository.findTodayGoals(memberId);

        if (todayGoals.isEmpty()) {
            return BigDecimal.ZERO;
        }

        long totalGoalCount = todayGoals.size();
        long completedGoalCount = todayGoals.stream()
                .mapToLong(goal -> goal.getStatus() == GoalStatus.COMPLETED ? 1 : 0)
                .sum();

        return BigDecimal.valueOf(completedGoalCount)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalGoalCount), RoundingMode.DOWN);
    }
}