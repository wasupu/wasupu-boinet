package io.wasupu.boinet.eventPublisher;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class StreamByEventTypeTest {

    @Test
    public void it_should_publish_in_correct_stream_by_event_type() {
        var event = Map.<String, Object>of("eventType", FIRST_EVENT_TYPE);

        var streamByEventTypeEventPublisher = new StreamByEventType();
        streamByEventTypeEventPublisher.register(FIRST_EVENT_TYPE, eventPublisher);

        streamByEventTypeEventPublisher.publish(event);

        verify(eventPublisher).publish(event);
    }

    @Mock
    private EventPublisher eventPublisher;

    private static final String FIRST_EVENT_TYPE = "firstEventType";
}
