package io.wasupu.boinet.subjects.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.subjects.Behaviour;
import io.wasupu.boinet.subjects.Subject;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MonthlyTest {

    @Test
    public void it_should_pay_on_day_25th() {
        when(world.getCurrentDateTime()).thenReturn(fixedDateTime.withDayOfMonth(25));

        monthlyRecurrentPayment.tick();

        verify(subjectBehaviour, atLeastOnce()).tick();
    }

    @Test
    public void it_should_not_pay_on_different_day_than_25th() {
        when(world.getCurrentDateTime()).thenReturn(fixedDateTime.withDayOfMonth(3));

        monthlyRecurrentPayment.tick();

        verify(subjectBehaviour, never()).tick();
    }

    @Before
    public void setupMonthlyRecurrentPayment() {
        monthlyRecurrentPayment = new Monthly(world,
            25,
            subjectBehaviour);
    }

    @Mock
    private Behaviour subjectBehaviour;

    private Monthly monthlyRecurrentPayment;

    private DateTime fixedDateTime = new DateTime(2017, 1, 1, 1, 1);

    @Mock
    private Subject subject;

    @Mock
    private World world;

}
