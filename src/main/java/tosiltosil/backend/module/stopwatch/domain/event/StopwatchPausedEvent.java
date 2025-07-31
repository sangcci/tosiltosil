package tosiltosil.backend.module.stopwatch.domain.event;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import tosiltosil.backend.module.stopwatch.domain.Stopwatch;

public record StopwatchPausedEvent(
        UUID memberId,
        Long goalId,
        String type,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Duration todayDuration
) {

    public static StopwatchPausedEvent of(
            final UUID memberId,
            final Long goalId,
            final Stopwatch stopwatch,
            final Duration todayDuration
    ) {
        return new StopwatchPausedEvent(
                memberId,
                goalId,
                "PAUSED",
                stopwatch.getStartedAt(),
                stopwatch.getEndedAt(),
                todayDuration
        );
    }
}
