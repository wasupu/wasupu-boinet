package io.wasupu.boinet.subjects.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.subjects.Behaviour;
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
    public void it_should_execute_on_day_in_the_week() {
        when(world.getCurrentDateTime()).thenReturn(new DateTime().withDayOfWeek(6));

        weekendRecurrentPayment.tick();

        verify(behaviour).tick();
    }

    @Test
    public void it_should_not_execute_on_different_day_than_6() {
        when(world.getCurrentDateTime()).thenReturn(new DateTime().withDayOfWeek(5));
        weekendRecurrentPayment.tick();

        verify(behaviour, never()).tick();
    }

    @Before
    public void setupMonthlyRecurrentPayment() {
        weekendRecurrentPayment = new Weekly(world,
            6,
            behaviour);
    }

    private Weekly weekendRecurrentPayment;

    @Mock
    private Behaviour behaviour;

    @Mock
    private World world;

}
