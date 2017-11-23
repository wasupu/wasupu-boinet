package io.wasupu.boinet;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import static net.logstash.logback.marker.Markers.appendEntries;

public class EventPublisher {

    public EventPublisher() {

    }

    public EventPublisher(String streamServiceApiKey, String streamServiceNamespace) {
        this.streamServiceApiKey = streamServiceApiKey;
        this.streamServiceNamespace = streamServiceNamespace;
    }

    public void publish(String streamId, Map<String, Object> event) {
        logger.info(appendEntries(event), streamId);
        if (streamServiceNamespace == null) return;

        publishInStreamService(streamId, event);
    }

    private void publishInStreamService(String streamId, Map<String, Object> event) {
        Map<String, Object> formattedEvent = formatEvent(event);

        buildRequest(streamId)
            .async()
            .post(Entity.entity(formattedEvent, MediaType.APPLICATION_JSON_TYPE), new InvocationCallback<Response>() {
                @Override
                public void completed(Response response) {
                    logger.info(appendEntries(ImmutableMap.of("status", response.getStatus())), "eventPublisher");
                }

                @Override
                public void failed(Throwable throwable) {
                    logger.error(appendEntries(ImmutableMap.of("message", "post(). error posting to stream service. Cause: " + throwable.getCause())), "eventPublisher");
                }
            });
    }

    private Map<String, Object> formatEvent(Map<String, Object> event) {
        Map<String, Object> newEvent = new HashMap<>(event);
        newEvent.put("date", simpleDateFormat.format(event.get("date")));
        return newEvent;
    }

    private Invocation.Builder buildRequest(String streamId) {
        return buildClient()
            .target(streamServiceNamespace)
            .path(String.format("/streams/%s:putRecord", streamId))
            .request()
            .header("API-KEY", streamServiceApiKey)
            .accept(MediaType.APPLICATION_JSON);
    }

    private Client buildClient() {
        return ClientBuilder.newClient();
    }

    private String streamServiceApiKey;
    private String streamServiceNamespace;

    private static Logger logger = LoggerFactory.getLogger(EventPublisher.class);

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
}
