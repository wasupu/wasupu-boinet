package io.wasupu.boinet.economicalSubjects.behaviours.balance;

import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.EconomicalSubject;
import io.wasupu.boinet.financial.Bank;
import io.wasupu.boinet.subjects.Behaviour;
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
    public void it_should_not_execute_behaviour_if_balance_is_below_the_threshold() {
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal("1"));

        whenBalanceIsBelowThreshold.tick();

        verify(behaviour, never()).tick();
    }

    @Test
    public void it_should_execute_the_behaviour_if_is_over_the_threshold() {
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal("10000"));

        whenBalanceIsBelowThreshold.tick();

        verify(behaviour).tick();
    }

    @Before
    public void setupBank() {
        when(economicalSubject.getIban()).thenReturn(IBAN);
        when(world.getBank()).thenReturn(bank);
    }

    @Before
    public void setupWhenBalanceIsBelowThreshold() {
        whenBalanceIsBelowThreshold = new WhenBalanceIsBelowThreshold(world,
            economicalSubject,
            new BigDecimal("1000"),
            behaviour);
    }

    @Mock
    private EconomicalSubject economicalSubject;

    @Mock
    private World world;

    @Mock
    private Bank bank;

    @Mock
    private Behaviour behaviour;

    private static final String IBAN = "2";

    private WhenBalanceIsBelowThreshold whenBalanceIsBelowThreshold;
}
