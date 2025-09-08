package tosiltosil.backend.module.progress.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import tosiltosil.backend.module.category.domain.event.CategoryDeletedEvent;
import tosiltosil.backend.module.goal.domain.event.GoalDeletedEvent;

@Component
@RequiredArgsConstructor
public class ProgressEventHandler {

    private final ProgressService progressService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCategoryDeletedEvent(final CategoryDeletedEvent event) {
        progressService.subtractTodayDuration(event.memberId(), event.deletedTotalDuration());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleGoalDeletedEvent(final GoalDeletedEvent event) {
        progressService.subtractTodayDuration(event.memberId(), event.deletedDuration());
    }
}