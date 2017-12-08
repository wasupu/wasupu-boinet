package io.wasupu.boinet.economicalSubjects.behaviours;

import io.wasupu.boinet.Bank;
import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.behaviours.InitialCapital;
import io.wasupu.boinet.population.Person;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InitialCapitalTest {

    @Test
    public void shouldDepositASocialSalaryInTheFirstTick() {
        when(person.getAge()).thenReturn(0L);
        when(world.getBank()).thenReturn(bank);
        when(person.getIban()).thenReturn(IBAN);

        initialCapital.tick();

        verify(bank).deposit(IBAN, InitialCapital.INITIAL_CAPITAL);
    }

    @Test
    public void shouldNotDepositAnySalaryInOtherTick() {
        when(person.getAge()).thenReturn(1L);
        initialCapital.tick();

        verify(bank, never()).deposit(any(), any());
    }

    @Before
    public void setupInitialCapital() {
        initialCapital = new InitialCapital(world, person);
    }

    private InitialCapital initialCapital;

    @Mock
    private World world;

    @Mock
    private Person person;

    private static final String IBAN = "2";

    @Mock
    private Bank bank;
}
