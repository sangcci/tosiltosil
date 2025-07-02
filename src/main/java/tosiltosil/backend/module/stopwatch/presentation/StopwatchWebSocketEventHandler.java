package tosiltosil.backend.module.stopwatch.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import tosiltosil.backend.module.stopwatch.domain.event.StopwatchPausedEvent;
import tosiltosil.backend.module.stopwatch.domain.event.StopwatchStartedEvent;
import tosiltosil.backend.module.stopwatch.presentation.dto.StopwatchPauseResponse;
import tosiltosil.backend.module.stopwatch.presentation.dto.StopwatchStartResponse;

@Component
@RequiredArgsConstructor
public class StopwatchWebSocketEventHandler {

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener
    public void handleStopwatchStartedEvent(final StopwatchStartedEvent event) {
        StopwatchStartResponse dto = StopwatchStartResponse.fromStartedEvent(event);
        messagingTemplate.convertAndSend("/topic/members/" + dto.memberId() + "/stopwatch", dto);
    }

    @TransactionalEventListener
    public void handleStopwatchPausedEvent(final StopwatchPausedEvent event) {
        StopwatchPauseResponse dto = StopwatchPauseResponse.fromPausedEvent(event);
        messagingTemplate.convertAndSend("/topic/members/" + dto.memberId() + "/stopwatch", dto);
    }
}
