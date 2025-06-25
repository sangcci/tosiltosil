package tosiltosil.backend.module.stopwatch.domain.event;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public record StopwatchStatusChangedEvent(
        UUID memberId,
        String type,
        String startTime,
        String totalTime
) {

    public static StopwatchStatusChangedEvent of(
            final UUID memberId,
            final String type,
            final LocalDateTime startTime,
            final Duration totalTime
    ) {
        return new StopwatchStatusChangedEvent(
                memberId,
                type,
                startTime.toString(),
                totalTime.toString()
        );
    }
}
