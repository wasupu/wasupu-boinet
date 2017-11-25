package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.Bank;
import io.wasupu.boinet.World;
import io.wasupu.boinet.population.Person;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StopWhenBalanceIsLessAThresholdTest {

    @Test
    public void shouldNotExecuteBehaviourIfBalanceIsBelowTheThreshold() {
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal("300"));

        stopWhenBalanceIsLessThreshold.tick();

        verify(personBehaviour, never()).tick();
    }

    @Test
    public void shouldExecuteTheBehaviourIfIsUpperTheThreshold() {
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal("6001"));

        stopWhenBalanceIsLessThreshold.tick();

        verify(personBehaviour).tick();
    }

    @Before
    public void setupBank() {
        when(person.getIban()).thenReturn(IBAN);
        when(world.getBank()).thenReturn(bank);
    }

    @Before
    public void setupGoToCountryside() {
        stopWhenBalanceIsLessThreshold = new StopWhenBalanceIsLessThreshold(world,
            person,
            new BigDecimal("1000"),
            personBehaviour);
    }

    @Mock
    private Person person;

    @Mock
    private World world;

    @Mock
    private Bank bank;

    @Mock
    private PersonBehaviour personBehaviour;

    private static final String IBAN = "2";

    private StopWhenBalanceIsLessThreshold stopWhenBalanceIsLessThreshold;
}
