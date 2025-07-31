package tosiltosil.backend.module.goal.domain.event;

import java.time.Duration;
import java.util.UUID;

public record GoalDeletedEvent(
        UUID memberId,
        Duration deletedDuration
) {

    public static GoalDeletedEvent of(final UUID memberId, final Duration deletedDuration) {
        return new GoalDeletedEvent(memberId, deletedDuration);
    }
}
