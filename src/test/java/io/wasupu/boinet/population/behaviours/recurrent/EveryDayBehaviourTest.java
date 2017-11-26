package io.wasupu.boinet.population.behaviours.recurrent;


import io.wasupu.boinet.World;
import io.wasupu.boinet.population.Person;
import io.wasupu.boinet.population.behaviours.PersonBehaviour;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EveryDayBehaviourTest {

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
        eatEveryDay = new EveryDayBehaviour(world, person, personBehaviour);
    }

    private EveryDayBehaviour eatEveryDay;

    @Mock
    private PersonBehaviour personBehaviour;

    @Mock
    private Person person;

    @Mock
    private World world;

}
