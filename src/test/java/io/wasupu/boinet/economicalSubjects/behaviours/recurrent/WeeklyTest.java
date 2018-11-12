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
public class WeeklyTest {

    @Test
    public void shouldPayOnDayInWeek() {
        when(world.getCurrentDateTime()).thenReturn(new DateTime().withDayOfWeek(6));

        weekendRecurrentPayment.tick();

        verify(personBehaviour).tick();
    }

    @Test
    public void shouldNotPayOnDifferentDayThan6() {
        when(world.getCurrentDateTime()).thenReturn(new DateTime().withDayOfWeek(5));
        weekendRecurrentPayment.tick();

        verify(personBehaviour, never()).tick();
    }

    @Before
    public void setupMonthlyRecurrentPayment() {
        weekendRecurrentPayment = new Weekly(world,
            person,
            6,
            personBehaviour);
    }

    private Weekly weekendRecurrentPayment;

    @Mock
    private EconomicalSubjectBehaviour personBehaviour;

    @Mock
    private Person person;

    @Mock
    private World world;

}
