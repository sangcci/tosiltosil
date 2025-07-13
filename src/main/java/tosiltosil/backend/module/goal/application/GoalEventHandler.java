package tosiltosil.backend.module.goal.application;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import tosiltosil.backend.common.domain.exception.NotFoundException;
import tosiltosil.backend.module.goal.domain.Goal;
import tosiltosil.backend.module.goal.domain.GoalRepository;
import tosiltosil.backend.module.stopwatch.domain.event.StopwatchPausedEvent;

@Component
@RequiredArgsConstructor
public class GoalEventHandler {

    private final GoalRepository goalRepository;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void addDuration(final StopwatchPausedEvent event) {
        Goal goal = goalRepository.findById(event.goalId())
                .orElseThrow(() -> new NotFoundException("목표가 존재하지 않습니다."));

        Duration addedDuration = Duration.between(event.startTime(), event.endTime());
        goal.addDuration(addedDuration);
    }
}