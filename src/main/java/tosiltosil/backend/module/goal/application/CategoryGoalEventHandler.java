package tosiltosil.backend.module.goal.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import tosiltosil.backend.module.category.domain.event.CategoryUpdatedEvent;

@Component
@RequiredArgsConstructor
public class CategoryGoalEventHandler {

    private final CategoryGoalService categoryGoalService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void handleCategoryUpdated(final CategoryUpdatedEvent event) {
        categoryGoalService.updateGoalsCategoryFromToday(
                event.memberId(),
                event.oldCategoryId(),
                event.newCategoryId()
        );
    }
}