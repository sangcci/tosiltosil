package tosiltosil.backend.common.messaging;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Events {

    private static ApplicationEventPublisher publisher;

    public static void setPublisher(final ApplicationEventPublisher publisher) {
        Events.publisher = publisher;
    }

    public static void raise(final Object event) {
        if (publisher != null) {
            publisher.publishEvent(event);
        }
    }
}
