package tosiltosil.backend.module.stopwatch.domain.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import tosiltosil.backend.module.stopwatch.domain.Stopwatch;

public record StopwatchPausedEvent(
        UUID memberId,
        @JsonIgnore
        Long goalId,
        String type,
        LocalDateTime startTime,
        @JsonIgnore
        LocalDateTime endTime,
        Duration totalTime
) {

    public static StopwatchPausedEvent of(
            final UUID memberId,
            final Long goalId,
            final Stopwatch stopwatch,
            final Duration totalTime
    ) {
        return new StopwatchPausedEvent(
                memberId,
                goalId,
                "PAUSED",
                stopwatch.getStartedAt(),
                stopwatch.getEndedAt(),
                totalTime
        );
    }
}
