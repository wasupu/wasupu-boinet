package io.wasupu.boinet.eventPublisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static net.logstash.logback.marker.Markers.appendEntries;

public class LogEventPublisher implements EventPublisher {

    public LogEventPublisher(String streamId) {
        this.streamId = streamId;
    }

    @Override
    public void publish(Map<String, Object> event) {
        logger.info(appendEntries(event), streamId);
    }

    private static Logger logger = LoggerFactory.getLogger(LogEventPublisher.class);

    private String streamId;

}
