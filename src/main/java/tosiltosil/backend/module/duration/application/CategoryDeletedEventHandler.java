package tosiltosil.backend.module.duration.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import tosiltosil.backend.module.category.domain.event.CategoryDeletedEvent;

@Component
@RequiredArgsConstructor
public class CategoryDeletedEventHandler {

    private final DurationService durationService;

    // TODO: Transaction 구성 고민
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCategoryDeletedEvent(final CategoryDeletedEvent event) {
        durationService.subtractTodayDuration(event.memberId(), event.deletedTotalDuration());
    }
}