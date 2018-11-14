package io.wasupu.boinet.eventPublisher;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class StreamByEventType implements EventPublisher {

    public void register(String eventType, EventPublisher eventPublisher) {
        eventPublishers.put(eventType, eventPublisher);
    }

    public void publish(Map<String, Object> event) {

        Optional.ofNullable(eventPublishers.get(event.get("eventType")))
            .ifPresent(eventPublisher -> eventPublisher.publish(event));
    }

    private Map<String, EventPublisher> eventPublishers = new HashMap<>();
}
