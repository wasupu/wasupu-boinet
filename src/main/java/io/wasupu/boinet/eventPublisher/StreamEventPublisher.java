package io.wasupu.boinet.eventPublisher;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;
import static net.logstash.logback.marker.Markers.appendEntries;

public class StreamEventPublisher implements EventPublisher {

    public StreamEventPublisher(String streamId, String streamServiceApiKey, String streamServiceNamespace) {
        this.streamServiceApiKey = streamServiceApiKey;
        this.streamServiceNamespace = streamServiceNamespace;
        this.streamId = streamId;
    }

    @Override
    public void publish(Map<String, Object> event) {
        logRelevantEvents(event);
        publishInStreamService(event);
    }

    private void logRelevantEvents(Map<String, Object> event) {
        if (event.get("eventType") == null) return;

        try {
            sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        logger.info(appendEntries(event), streamId);
    }

    private void publishInStreamService(Map<String, Object> event) {
        bufferEvent(formatEvent(event));

        if (eventsBuffer.size() >= BATCH_SIZE) {
            buildRequest()
                .async()
                .post(Entity.entity(Map.of("records", eventsBuffer), MediaType.APPLICATION_JSON_TYPE), new InvocationCallback<Response>() {
                    @Override
                    public void completed(Response response) {
                        logger.info(appendEntries(Map.of("status", response.getStatus())), "eventPublisher");
                    }

                    @Override
                    public void failed(Throwable throwable) {
                        logger.error(appendEntries(Map.of("message", "post(). error posting to stream service. Cause: " + throwable.getCause())), "eventPublisher");
                    }
                });
            clearEventsBuffer();
        }
    }

    private void clearEventsBuffer() {
        eventsBuffer = List.of();
    }

    private void bufferEvent(Map<String, Object> event) {
        eventsBuffer = ImmutableList
            .<Map<String, Object>>builder()
            .addAll(eventsBuffer)
            .add(event)
            .build();
    }

    private Map<String, Object> formatEvent(Map<String, Object> event) {
        var newEvent = new HashMap<>(event);
        newEvent.put("date", dateFormat.format(event.get("date")));
        return newEvent;
    }

    private Invocation.Builder buildRequest() {
        return buildClient()
            .target(streamServiceNamespace)
            .path(String.format("/streams/%s:putRecordBatch", streamId))
            .request()
            .header("API-KEY", streamServiceApiKey)
            .accept(MediaType.APPLICATION_JSON);
    }

    private Client buildClient() {
        return ClientBuilder.newClient();
    }

    private String streamServiceApiKey;
    private String streamServiceNamespace;

    private static Logger logger = LoggerFactory.getLogger(StreamEventPublisher.class);

    private Collection<Map<String, Object>> eventsBuffer = List.of();

    private static final ISO8601DateFormat dateFormat = new ISO8601DateFormat();

    private static final Integer BATCH_SIZE = 50;

    private String streamId;

}
