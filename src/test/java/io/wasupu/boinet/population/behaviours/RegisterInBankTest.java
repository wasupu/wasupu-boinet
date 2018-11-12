package io.wasupu.boinet.population.behaviours;


import io.wasupu.boinet.World;
import io.wasupu.boinet.financial.Bank;
import io.wasupu.boinet.population.Person;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RegisterInBankTest {

    @Test
    public void it_should_register_in_bank_at_first_tick() {
        new RegisterInBank(world, person).tick();

        verify(bank).registerUser(person);
    }

    @Test
    public void it_should_not_register_in_bank_after_first_tick() {
        var behaviour = new RegisterInBank(world, person);
        when(person.getAge()).thenReturn(1L);

        behaviour.tick();

        verify(bank, never()).registerUser(person);
    }

    @Before
    public void setupWorldBank() {
        when(world.getBank()).thenReturn(bank);
    }

    @Mock
    private World world;

    @Mock
    private Person person;

    @Mock
    private Bank bank;

}