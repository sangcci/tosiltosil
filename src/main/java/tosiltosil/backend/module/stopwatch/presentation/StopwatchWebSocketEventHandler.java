package tosiltosil.backend.module.stopwatch.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import tosiltosil.backend.module.stopwatch.domain.event.StopwatchStatusChangedEvent;

@Component
@RequiredArgsConstructor
public class StopwatchWebSocketEventHandler {

    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleStopwatchStatueChangedEvent(final StopwatchStatusChangedEvent event) {
        messagingTemplate.convertAndSend("/topic/members/" + event.memberId() + "/stopwatch", event);
    }
}
