package io.wasupu.boinet.economicalSubjects.behaviours.recurrent;


import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;
import io.wasupu.boinet.population.Person;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EveryDayTest {

    @Test
    public void shouldEatEveryTickAfterTwoTicks() {
        when(person.getAge()).thenReturn(2L);

        eatEveryDay.tick();
        eatEveryDay.tick();
        eatEveryDay.tick();

        verify(personBehaviour, times(3)).tick();
    }

    @Before
    public void setUpEveryDayRecurrentPayment() {
        eatEveryDay = new EveryDay(world, person, personBehaviour);
    }

    private EveryDay eatEveryDay;

    @Mock
    private EconomicalSubjectBehaviour personBehaviour;

    @Mock
    private Person person;

    @Mock
    private World world;

}
