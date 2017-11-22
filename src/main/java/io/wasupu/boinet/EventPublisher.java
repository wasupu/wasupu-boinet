package io.wasupu.boinet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static net.logstash.logback.marker.Markers.appendEntries;

public class EventPublisher {

    public EventPublisher(String semaasApiKey, String semaasNamespace) {

    }

    public void publish(String streamId, Map<String, Object> event) {
        logger.info(appendEntries(event), streamId);
    }

    private static Logger logger = LoggerFactory.getLogger(EventPublisher.class);

}
