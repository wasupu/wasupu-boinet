package io.wasupu.boinet.economicalSubjects.behaviours.recurrent;


import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.EconomicalSubject;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EveryDayTest {

    @Test
    public void it_should_eat_every_tick_after_two_ticks() {
        when(economicalSubject.getAge()).thenReturn(2L);

        everyDay.tick();
        everyDay.tick();
        everyDay.tick();

        verify(economicalSubjectBehaviour, times(3)).tick();
    }

    @Before
    public void setUpEveryDayRecurrentPayment() {
        everyDay = new EveryDay(world, economicalSubject, economicalSubjectBehaviour);
    }

    private EveryDay everyDay;

    @Mock
    private EconomicalSubjectBehaviour economicalSubjectBehaviour;

    @Mock
    private EconomicalSubject economicalSubject;

    @Mock
    private World world;

}
