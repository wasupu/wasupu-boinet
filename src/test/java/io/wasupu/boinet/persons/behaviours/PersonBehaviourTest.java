package io.wasupu.boinet.persons.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.persons.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PersonBehaviourTest {

    @Test
    public void shouldAddTheBehaviourToThePersonWhenCreate(){

        new PersonBehaviour(world,person) {
            @Override
            public void tick() {
            }
        };

        verify(person).listenTicks(any());
    }

    @Mock
    private Person person;

    @Mock
    private World world;

}

