package io.wasupu.boinet.eventPublisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static net.logstash.logback.marker.Markers.appendEntries;

public class LogEventPublisher implements EventPublisher {

    @Override
    public void publish(Map<String, Object> event) {
        logger.info(appendEntries(event), "");
    }

    private static Logger logger = LoggerFactory.getLogger(LogEventPublisher.class);


}
