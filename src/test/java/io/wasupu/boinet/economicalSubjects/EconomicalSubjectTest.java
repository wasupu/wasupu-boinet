package io.wasupu.boinet.economicalSubjects;

import io.wasupu.boinet.GPS;
import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;
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
public class EconomicalSubjectTest {

    @Test
    public void it_should_add_a_new_behaviour_to_subject() {
        economicalSubject.addBehaviour(economicalSubjectBehaviour);

        assertTrue("The behaviour must to exists", economicalSubject.existsBehaviour(economicalSubjectBehaviour));
    }

    @Test
    public void it_should_omit_behaviour_from_subject() {
        when(economicalSubjectBehaviour.getIdentifier()).thenReturn(ECONOMICAL_SUBJECT_BEHAVIOUR_IDENTIFIER);
        economicalSubject.addBehaviour(economicalSubjectBehaviour);

        economicalSubject.removeBehaviour(economicalSubjectBehaviour);

        assertFalse("The behaviour must to exists", economicalSubject.existsBehaviour(economicalSubjectBehaviour));
    }

    @Before
    public void setWorld() {
        when(world.getGPS()).thenReturn(gps);
        when(gps.coordinates()).thenReturn(Pair.of(10.2, 20.3));
    }

    @Before
    public void setupEconomicalSubject() {
        economicalSubject = new EconomicalSubject(ECONOMICAL_SUBJECT_IDENTIFIER, world) {};
    }

    private EconomicalSubject economicalSubject;

    private static String ECONOMICAL_SUBJECT_IDENTIFIER = "identifier";

    private static String ECONOMICAL_SUBJECT_BEHAVIOUR_IDENTIFIER = "behaviourIdentifier";

    @Mock
    private World world;

    @Mock
    private EconomicalSubjectBehaviour economicalSubjectBehaviour;

    @Mock
    private GPS gps;
}


