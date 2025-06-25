package tosiltosil.backend.module.goal.domain.service;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tosiltosil.backend.module.goal.domain.Goal;
import tosiltosil.backend.module.goal.domain.GoalRepository;

@Component
@RequiredArgsConstructor
public class GoalDomainService {

    private final GoalRepository goalRepository;

    public void validateCreation(final UUID memberId) {
        List<Goal> todayGoals = goalRepository.findTodayGoalsByMemberId(memberId);
        Duration todayTotalTime = todayGoals.stream()
                .map(Goal::getTotalTime)
                .reduce(Duration.ZERO, Duration::plus);

        Duration max = Duration.ofHours(24);
        if (todayTotalTime.compareTo(max) >= 0) {
            throw new IllegalArgumentException("일일 목표 총 시간인 24시간을 초과하여 목표를 생성할 수 없습니다.");
        }
    }
}
