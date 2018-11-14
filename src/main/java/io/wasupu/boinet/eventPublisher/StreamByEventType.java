package io.wasupu.boinet.eventPublisher;

import java.util.HashMap;
import java.util.Map;

public class StreamByEventType implements EventPublisher {

    public void register(String eventType, EventPublisher eventPublisher) {
        eventPublishers.put(eventType, eventPublisher);
    }

    public void publish(Map<String, Object> event) {
        eventPublishers.get(event.get("eventType")).publish(event);
    }

    private Map<String, EventPublisher> eventPublishers = new HashMap<>();
}
