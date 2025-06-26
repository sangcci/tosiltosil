package tosiltosil.backend.module.stopwatch.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import tosiltosil.backend.module.stopwatch.domain.event.StopwatchPausedEvent;
import tosiltosil.backend.module.stopwatch.domain.event.StopwatchStartedEvent;

@Component
@RequiredArgsConstructor
public class StopwatchWebSocketEventHandler {

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener
    public void handleStopwatchStartedEvent(final StopwatchStartedEvent event) {
        messagingTemplate.convertAndSend("/topic/members/" + event.memberId() + "/stopwatch", event);
    }

    @TransactionalEventListener
    public void handleStopwatchPausedEvent(final StopwatchPausedEvent event) {
        messagingTemplate.convertAndSend("/topic/members/" + event.memberId() + "/stopwatch", event);
    }
}
