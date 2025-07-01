package tosiltosil.backend.module.stopwatch.presentation.dto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import tosiltosil.backend.module.stopwatch.domain.event.StopwatchPausedEvent;
import tosiltosil.backend.module.stopwatch.domain.event.StopwatchStartedEvent;

public record StopwatchEventDto(
        UUID memberId,
        String type,
        LocalDateTime startTime,
        Duration totalTime
) {

    public static StopwatchEventDto fromStartedEvent(final StopwatchStartedEvent event) {
        return new StopwatchEventDto(
                event.memberId(),
                event.type(),
                event.startTime(),
                event.totalTime()
        );
    }

    public static StopwatchEventDto fromPausedEvent(final StopwatchPausedEvent event) {
        return new StopwatchEventDto(
                event.memberId(),
                event.type(),
                event.startTime(),
                event.totalTime()
        );
    }
}