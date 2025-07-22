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

@Component
@RequiredArgsConstructor
public class GoalDomainService {

    private static final BigDecimal MAX_PERCENTAGE = new BigDecimal("100");

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

        long totalTimeInSeconds = todayGoals.stream()
                .mapToLong(goal -> goal.getTotalTime().toSeconds())
                .sum();

        long achievedTimeInSeconds = todayGoals.stream()
                .mapToLong(goal -> goal.getDuration().toSeconds())
                .sum();

        if (totalTimeInSeconds == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal percentage = BigDecimal.valueOf(achievedTimeInSeconds)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(totalTimeInSeconds), RoundingMode.HALF_UP);

        return percentage.min(MAX_PERCENTAGE);
    }
}