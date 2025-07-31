package tosiltosil.backend.module.stopwatch.domain.event;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import tosiltosil.backend.module.stopwatch.domain.Stopwatch;

public record StopwatchStartedEvent(
        UUID memberId,
        String type,
        LocalDateTime startTime,
        Duration todayDuration
) {

    public static StopwatchStartedEvent of(
            final UUID memberId,
            final Stopwatch stopwatch,
            final Duration todayDuration
    ) {
        return new StopwatchStartedEvent(
                memberId,
                "STARTED",
                stopwatch.getStartedAt(),
                todayDuration
        );
    }
}
