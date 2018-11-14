package io.wasupu.boinet.eventPublisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.google.common.collect.ImmutableMap;
import com.xebialabs.restito.semantics.Call;
import com.xebialabs.restito.semantics.Condition;
import com.xebialabs.restito.server.StubServer;
import io.wasupu.boinet.eventPublisher.StreamEventPublisher;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.builder.verify.VerifyHttp.verifyHttp;
import static com.xebialabs.restito.semantics.Action.status;
import static com.xebialabs.restito.semantics.Condition.post;
import static com.xebialabs.restito.semantics.Condition.withHeader;
import static io.wasupu.boinet.PollingResultVerifier.retry;

public class StreamEventPublisherTest {

    @Test
    public void it_should_publish_a_batch_of_events() {
        var eventPublisher = new StreamEventPublisher(STREAM_ID, STREAM_SERVICE_API_KEY, STREAM_SERVICE_NAMESPACE);
        var eventsBatch = IntStream.range(0, BATCH_SIZE)
            .mapToObj(this::buildTestEvent)
            .collect(Collectors.toList());

        eventsBatch.forEach(eventPublisher::publish);

        retry(() -> verifyHttp(server))
            .subscribe(verifyHttp -> verifyHttp.once(
                post(EXPECTED_PATH),
                withHeader("API-KEY", STREAM_SERVICE_API_KEY),
                withNumberOfRecords(BATCH_SIZE)))
            .run();
    }

    @Test
    public void it_should_publish_a_batch_with_the_right_events() {
        var eventPublisher = new StreamEventPublisher(STREAM_ID, STREAM_SERVICE_API_KEY, STREAM_SERVICE_NAMESPACE);
        var eventsBatch = IntStream.range(0, BATCH_SIZE)
            .mapToObj(this::buildTestEvent)
            .collect(Collectors.toList());

        eventsBatch.forEach(eventPublisher::publish);

        retry(() -> verifyHttp(server))
            .subscribe(verifyHttp -> verifyHttp.once(
                post(EXPECTED_PATH),
                withHeader("API-KEY", STREAM_SERVICE_API_KEY),
                withRecords(eventsBatch)))
            .run();
    }

    @Test
    public void it_should_publish_two_batch_of_events() {
        var eventPublisher = new StreamEventPublisher(STREAM_ID, STREAM_SERVICE_API_KEY, STREAM_SERVICE_NAMESPACE);
        var eventsBatch = IntStream.range(0, BATCH_SIZE * 2)
            .mapToObj(this::buildTestEvent)
            .collect(Collectors.toList());

        eventsBatch.forEach(eventPublisher::publish);

        retry(() -> verifyHttp(server))
            .subscribe(verifyHttp -> verifyHttp.times(2,
                post(EXPECTED_PATH),
                withHeader("API-KEY", STREAM_SERVICE_API_KEY),
                withNumberOfRecords(BATCH_SIZE)))
            .run();
    }

    @Test
    public void it_should_publish_the_two_right_batch_of_events() {
        var eventPublisher = new StreamEventPublisher(STREAM_ID, STREAM_SERVICE_API_KEY, STREAM_SERVICE_NAMESPACE);
        var eventsBatch = IntStream.range(0, BATCH_SIZE * 2)
            .mapToObj(this::buildTestEvent)
            .collect(Collectors.toList());

        eventsBatch.forEach(eventPublisher::publish);

        retry(() -> verifyHttp(server))
            .subscribe(verifyHttp -> verifyHttp.once(
                post(EXPECTED_PATH),
                withHeader("API-KEY", STREAM_SERVICE_API_KEY),
                withRecords(eventsBatch.stream().limit(BATCH_SIZE).collect(Collectors.toList()))))
            .subscribe(verifyHttp -> verifyHttp.once(
                post(EXPECTED_PATH),
                withHeader("API-KEY", STREAM_SERVICE_API_KEY),
                withRecords(eventsBatch.stream().skip(BATCH_SIZE).collect(Collectors.toList()))))
            .run();
    }

    private Map<String, Object> buildTestEvent(Integer index) {
        return Map.of(
            "event", index,
            "date", new Date());
    }

    private Condition withNumberOfRecords(Integer expectedSize) {
        return Condition.custom(call -> {
            var postBody = getBodyAsMap(call);
            return expectedSize.equals(((Collection<Map<String, Object>>) postBody.get("records")).size());
        });
    }

    private Condition withRecords(Collection<Map<String, Object>> expectedEvents) {
        return Condition.custom(call -> {
            var postBody = getBodyAsMap(call);
            var records = (Collection<Map<String, Object>>) postBody.get("records");
            return expectedEvents
                .stream()
                .map(expectedEvent -> {
                    HashMap<String, Object> expectedEventWithFormattedDate = new HashMap<>(expectedEvent);
                    expectedEventWithFormattedDate.put("date", dateFormat.format(expectedEvent.get("date")));
                    return expectedEventWithFormattedDate;
                })
                .collect(Collectors.toList())
                .equals(records);
        });
    }

    private Map<String, Object> getBodyAsMap(Call call) {
        Map<String, Object> postBody;
        try {
            postBody = objectMapper.readValue(call.getPostBody(), Map.class);
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
        return postBody;
    }

    @Before
    public void start() {
        server = new StubServer(PORT).run();

        whenHttp(server)
            .match(
                post(EXPECTED_PATH),
                withHeader("API-KEY", STREAM_SERVICE_API_KEY))
            .then(status(HttpStatus.CREATED_201));
    }

    @After
    public void stop() {
        server.stop();
    }

    private StubServer server;

    private static final Integer PORT = 31023;
    private static final String STREAM_ID = "certainEventStream";
    private static final String STREAM_SERVICE_API_KEY = "apiapi";
    private static final String STREAM_SERVICE_NAMESPACE_PATH = "/test/namespace";
    private static final String STREAM_SERVICE_NAMESPACE = String.format("http://127.0.0.1:%s%s", PORT, STREAM_SERVICE_NAMESPACE_PATH);
    private static final String EXPECTED_PATH = String.format("%s/streams/%s:putRecordBatch", STREAM_SERVICE_NAMESPACE_PATH, STREAM_ID);

    private ObjectMapper objectMapper = new ObjectMapper();
    private ISO8601DateFormat dateFormat = new ISO8601DateFormat();

    private static final int BATCH_SIZE = 50;
}