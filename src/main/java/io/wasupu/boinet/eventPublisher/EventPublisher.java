package io.wasupu.boinet.eventPublisher;

import java.util.Map;

public interface EventPublisher {
    void publish(Map<String, Object> event);
}
