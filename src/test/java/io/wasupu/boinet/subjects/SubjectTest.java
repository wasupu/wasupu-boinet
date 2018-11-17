package io.wasupu.boinet.subjects;

import io.wasupu.boinet.GPS;
import io.wasupu.boinet.World;
import io.wasupu.boinet.subjects.Behaviour;
import io.wasupu.boinet.subjects.Subject;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SubjectTest {

    @Test
    public void it_should_add_a_new_behaviour_to_subject() {
        subject.addBehaviour(behaviour);

        assertTrue("The behaviour must to exists", subject.existsBehaviour(behaviour));
    }

    @Test
    public void it_should_omit_behaviour_from_subject() {
        when(behaviour.getIdentifier()).thenReturn(SUBJECT_BEHAVIOUR_IDENTIFIER);
        subject.addBehaviour(behaviour);

        subject.removeBehaviour(behaviour);

        assertFalse("The behaviour must to exists", subject.existsBehaviour(behaviour));
    }

    @Before
    public void setWorld() {
        when(world.getGPS()).thenReturn(gps);
        when(gps.coordinates()).thenReturn(Pair.of(10.2, 20.3));
    }

    @Before
    public void setupEconomicalSubject() {
        subject = new Subject(ECONOMICAL_SUBJECT_IDENTIFIER, world) {
        };
    }

    private Subject subject;

    private static String ECONOMICAL_SUBJECT_IDENTIFIER = "identifier";

    private static String SUBJECT_BEHAVIOUR_IDENTIFIER = "behaviourIdentifier";

    @Mock
    private World world;

    @Mock
    private Behaviour behaviour;

    @Mock
    private GPS gps;
}


