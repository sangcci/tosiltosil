package tosiltosil.backend.module.category.domain.event;

import java.time.Duration;
import java.util.UUID;

public record CategoryDeletedEvent(
        UUID memberId,
        Long categoryId,
        Duration deletedTotalDuration
) {
    
    public static CategoryDeletedEvent of(final UUID memberId, final Long categoryId, final Duration deletedTotalDuration) {
        return new CategoryDeletedEvent(memberId, categoryId, deletedTotalDuration);
    }
}