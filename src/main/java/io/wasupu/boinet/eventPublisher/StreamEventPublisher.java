package io.wasupu.boinet.eventPublisher;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.logstash.logback.marker.Markers.appendEntries;

public class StreamEventPublisher implements EventPublisher {

    public StreamEventPublisher(String streamId, String streamServiceApiKey, String streamServiceNamespace) {
        this.streamServiceApiKey = streamServiceApiKey;
        this.streamServiceNamespace = streamServiceNamespace;
        this.streamId = streamId;
        this.client = buildClient();
    }

    @Override
    public void publish(Map<String, Object> event) {
        publishInStreamService(event);
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
        return client
            .target(streamServiceNamespace)
            .path(String.format("/streams/%s:putRecordBatch", streamId))
            .request()
            .header("API-KEY", streamServiceApiKey)
            .accept(MediaType.APPLICATION_JSON);
    }

    private Client buildClient() {
        TrustManager[] noopTrustManager = new TrustManager[]{
            new X509TrustManager() {

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };

        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("ssl");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sc.init(null, noopTrustManager, null);
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return ClientBuilder.newBuilder().sslContext(sc).build();
    }

    private String streamServiceApiKey;
    private String streamServiceNamespace;

    private static Logger logger = LoggerFactory.getLogger(StreamEventPublisher.class);

    private Collection<Map<String, Object>> eventsBuffer = List.of();

    private static final ISO8601DateFormat dateFormat = new ISO8601DateFormat();

    private static final Integer BATCH_SIZE = 50;

    private String streamId;

    private Client client;

}
