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
public class MonthlyBehaviourTest {

    @Test
    public void shouldPayOnDay25th() {
        when(world.getCurrentDateTime()).thenReturn(fixedDateTime.withDayOfMonth(25));

        monthlyRecurrentPayment.tick();

        verify(personBehaviour, atLeastOnce()).tick();
    }

    @Test
    public void shouldNotPayOnDifferentDayThan25th() {
        when(world.getCurrentDateTime()).thenReturn(fixedDateTime.withDayOfMonth(3));

        monthlyRecurrentPayment.tick();

        verify(personBehaviour, never()).tick();
    }

    @Before
    public void setupMonthlyRecurrentPayment() {
        monthlyRecurrentPayment = new MonthlyBehaviour(world,
            person,
            25,
            personBehaviour);
    }

    @Mock
    private EconomicalSubjectBehaviour personBehaviour;

    private MonthlyBehaviour monthlyRecurrentPayment;

    private DateTime fixedDateTime = new DateTime(2017, 1, 1, 1, 1);

    @Mock
    private Person person;

    @Mock
    private World world;

}
