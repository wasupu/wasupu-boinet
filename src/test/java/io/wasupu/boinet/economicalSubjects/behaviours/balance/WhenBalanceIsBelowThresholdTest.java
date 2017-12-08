package io.wasupu.boinet.economicalSubjects.behaviours.balance;

import io.wasupu.boinet.financial.Bank;
import io.wasupu.boinet.World;
import io.wasupu.boinet.population.Person;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WhenBalanceIsBelowThresholdTest {

    @Test
    public void shouldNotExecuteBehaviourIfBalanceIsBelowTheThreshold() {
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal("1"));

        whenBalanceIsBelowThreshold.tick();

        verify(personBehaviour, never()).tick();
    }

    @Test
    public void shouldExecuteTheBehaviourIfIsOverTheThreshold() {
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal("10000"));

        whenBalanceIsBelowThreshold.tick();

        verify(personBehaviour).tick();
    }

    @Before
    public void setupBank() {
        when(person.getIban()).thenReturn(IBAN);
        when(world.getBank()).thenReturn(bank);
    }

    @Before
    public void setupGoToCountryside() {
        whenBalanceIsBelowThreshold = new WhenBalanceIsBelowThreshold(world,
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
    private EconomicalSubjectBehaviour personBehaviour;

    private static final String IBAN = "2";

    private WhenBalanceIsBelowThreshold whenBalanceIsBelowThreshold;
}
