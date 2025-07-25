package tosiltosil.backend.module.stopwatch.presentation.dto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import tosiltosil.backend.module.stopwatch.domain.event.StopwatchPausedEvent;

public record StopwatchPauseResponse(
        UUID memberId,
        String type,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Duration memberTodayDuration
) {

    public static StopwatchPauseResponse fromPausedEvent(final StopwatchPausedEvent event) {
        return new StopwatchPauseResponse(
                event.memberId(),
                event.type(),
                event.startTime(),
                event.endTime(),
                event.memberTodayDuration()
        );
    }
}
