package tosiltosil.backend.module.stopwatch.presentation.dto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import tosiltosil.backend.module.stopwatch.domain.event.StopwatchStartedEvent;

public record StopwatchStartResponse(
        UUID memberId,
        String type,
        LocalDateTime startTime,
        Duration totalTime
) {

    public static StopwatchStartResponse fromStartedEvent(final StopwatchStartedEvent event) {
        return new StopwatchStartResponse(
                event.memberId(),
                event.type(),
                event.startTime(),
                event.totalTime()
        );
    }
}