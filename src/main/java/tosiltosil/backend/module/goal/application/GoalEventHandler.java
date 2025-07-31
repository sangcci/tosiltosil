package tosiltosil.backend.module.goal.application;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import tosiltosil.backend.common.domain.exception.NotFoundException;
import tosiltosil.backend.module.category.domain.event.CategoryDeletedEvent;
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

        // 목표 완료되었는지 체크 및 반영
        goal.changeStatusToCompleted();
    }
    
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleCategoryDeletedEvent(final CategoryDeletedEvent event) {
        // 카테고리에 속한 목표 모두 삭제
        goalRepository.deleteAllByMemberIdAndCategoryId(event.memberId(), event.categoryId());
    }
}