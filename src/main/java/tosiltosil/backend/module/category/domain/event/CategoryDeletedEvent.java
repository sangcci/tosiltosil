package tosiltosil.backend.module.category.domain.event;

import java.time.Duration;
import java.util.UUID;

public record CategoryDeletedEvent(
        UUID memberId,
        Duration deletedTotalDuration
) {
    
    public static CategoryDeletedEvent of(final UUID memberId, final Duration deletedTotalDuration) {
        return new CategoryDeletedEvent(memberId, deletedTotalDuration);
    }
}