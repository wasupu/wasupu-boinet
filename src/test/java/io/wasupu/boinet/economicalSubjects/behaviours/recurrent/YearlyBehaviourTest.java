package io.wasupu.boinet.economicalSubjects.behaviours.recurrent;

import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;
import io.wasupu.boinet.population.Person;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class YearlyBehaviourTest {

    @Test
    public void shouldTriggerInConcreteDayOfYear() {
        when(world.getCurrentDateTime()).thenReturn(new DateTime().withDayOfYear(60));

        yearlyBehaviour.tick();

        verify(personBehaviour).tick();
    }

    @Test
    public void shouldNotTriggerInConcreteDayOfYear() {
        when(world.getCurrentDateTime()).thenReturn(new DateTime().withDayOfYear(5));

        yearlyBehaviour.tick();

        verify(personBehaviour,never()).tick();
    }

    @Before
    public void setupMonthlyRecurrentPayment() {
        yearlyBehaviour = new YearlyBehaviour(world,
            person,
            60,
            personBehaviour);
    }

    private YearlyBehaviour yearlyBehaviour;

    @Mock
    private World world;

    @Mock
    private Person person;

    @Mock
    private EconomicalSubjectBehaviour personBehaviour;
}
