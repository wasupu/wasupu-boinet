package io.wasupu.boinet.subjects.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.subjects.Behaviour;
import io.wasupu.boinet.subjects.behaviours.Yearly;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class YearlyTest {

    @Test
    public void it_should_trigger_in_concrete_day_of_year() {
        when(world.getCurrentDateTime()).thenReturn(new DateTime().withDayOfYear(60));

        yearlyBehaviour.tick();

        verify(behaviour).tick();
    }

    @Test
    public void it_should_not_trigger_in_concrete_day_of_year() {
        when(world.getCurrentDateTime()).thenReturn(new DateTime().withDayOfYear(5));

        yearlyBehaviour.tick();

        verify(behaviour,never()).tick();
    }

    @Before
    public void setupMonthlyRecurrentPayment() {
        yearlyBehaviour = new Yearly(world,
            60,
            behaviour);
    }

    private Yearly yearlyBehaviour;

    @Mock
    private World world;

    @Mock
    private Behaviour behaviour;
}
