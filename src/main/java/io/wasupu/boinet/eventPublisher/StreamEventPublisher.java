package io.wasupu.boinet.eventPublisher;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.google.common.collect.ImmutableList;
import org.glassfish.jersey.SslConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.*;

import static com.google.common.io.ByteStreams.toByteArray;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static net.logstash.logback.marker.Markers.appendEntries;

public class StreamEventPublisher implements EventPublisher {

    public StreamEventPublisher(String streamId, String streamServiceNamespace, String serverKeyStorePassphrase, String clientKeyStorePassphrase, Integer cadenceMilliseconds, Integer batchSize) {
        this.streamServiceNamespace = streamServiceNamespace;
        this.streamId = streamId;
        this.serverKeyStorePassphrase = serverKeyStorePassphrase;
        this.clientKeyStorePassphrase = clientKeyStorePassphrase;
        this.cadenceMilliseconds = cadenceMilliseconds;
        this.batchSize = batchSize;
        this.client = buildClient();

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                publishToStreamService();
            }
        }, 60000L, 10000L);
    }

    @Override
    public void publish(Map<String, Object> event) {
        bufferAndPublishInStreamService(event);
    }

    private void bufferAndPublishInStreamService(Map<String, Object> event) {
        bufferEvent(formatEvent(event));

        if (eventsBuffer.size() >= batchSize) {
            publishToStreamService();
        }
    }


    private synchronized void publishToStreamService() {
        if (eventsBuffer.isEmpty()) return;

        throttle();

        buildRequest()
            .async()
            .post(Entity.entity(Map.of("records", eventsBuffer), MediaType.APPLICATION_JSON_TYPE), new InvocationCallback<Response>() {
                @Override
                public void completed(Response response) {
                    logger.info(appendEntries(Map.of(
                        "eventType", "publishEventsSuccess",
                        "stream", streamId,
                        "status", response.getStatus())), "eventPublisher");
                }

                @Override
                public void failed(Throwable throwable) {
                    logger.error(appendEntries(Map.of(
                        "eventType", "publishEventsError",
                        "message", "post(). error posting to stream service. Cause: " + throwable.getCause(),
                        "stream", streamId)), "eventPublisher");
                }
            });
        clearEventsBuffer();
    }

    private void throttle() {
        try {
            MILLISECONDS.sleep(cadenceMilliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
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
            .accept(MediaType.APPLICATION_JSON);
    }

    private Client buildClient() {
        try {
            var sslContext = SslConfigurator.newInstance()
                .trustStoreBytes(toByteArray(getClass().getClassLoader().getResource("truststore.p12").openStream()))
                .trustStorePassword(serverKeyStorePassphrase)
                .keyStoreBytes(toByteArray(getClass().getClassLoader().getResource("keystore.p12").openStream()))
                .keyPassword(clientKeyStorePassphrase).createSSLContext();

            return ClientBuilder.newBuilder()
                .sslContext(sslContext)
                .build();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }


    private String streamServiceNamespace;

    private static Logger logger = LoggerFactory.getLogger(StreamEventPublisher.class);

    private Collection<Map<String, Object>> eventsBuffer = List.of();

    private static final ISO8601DateFormat dateFormat = new ISO8601DateFormat();

    private String streamId;

    private Client client;

    private String serverKeyStorePassphrase;

    private String clientKeyStorePassphrase;

    private Integer cadenceMilliseconds;

    private Integer batchSize;
}
