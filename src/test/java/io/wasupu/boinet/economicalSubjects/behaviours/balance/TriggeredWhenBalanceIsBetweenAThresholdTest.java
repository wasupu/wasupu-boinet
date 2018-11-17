package io.wasupu.boinet.economicalSubjects.behaviours.balance;

import io.wasupu.boinet.World;
import io.wasupu.boinet.companies.Company;
import io.wasupu.boinet.economicalSubjects.EconomicalSubject;
import io.wasupu.boinet.financial.Bank;
import io.wasupu.boinet.subjects.Behaviour;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TriggeredWhenBalanceIsBetweenAThresholdTest {

    @Test
    public void it_should_not_execute_if_I_have_more_than_6000_euro() {
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal("6001"));

        triggeredWhenBalanceBetweenAThreshold.tick();

        verify(company, never()).buyProduct(any(), any(), any());
    }

    @Test
    public void it_should_execute_on_weekends_when_I_have_more_than_6000_euro() {
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal("6001"));

        triggeredWhenBalanceBetweenAThreshold.tick();

        verify(behaviour, atLeastOnce()).tick();
    }

    @Test
    public void it_should_not_execute_on_weekends_when_I_have_3000_euro() {
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal("1000"));

        triggeredWhenBalanceBetweenAThreshold.tick();

        verify(behaviour, never()).tick();
    }

    @Test
    public void it_should_keep_execute_on_weekends_when_I_have_3000_euro() {
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal("6001"));

        triggeredWhenBalanceBetweenAThreshold.tick();

        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal("3000"));

        triggeredWhenBalanceBetweenAThreshold.tick();

        verify(behaviour, times(2)).tick();
    }

    @Test
    public void it_should_stop_execute_on_weekends_when_I_have_less_of_1000_euro() {
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal("999.99"));

        triggeredWhenBalanceBetweenAThreshold.tick();

        verify(behaviour, never()).tick();
    }

    @Before
    public void setupBank() {
        when(economicalSubject.getIban()).thenReturn(IBAN);
        when(world.getBank()).thenReturn(bank);
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal(12));
    }

    @Before
    public void setupGoToCountryside() {
        triggeredWhenBalanceBetweenAThreshold = new TriggeredWhenBalanceBetweenAThreshold(world,
            economicalSubject,
            new BigDecimal("1000"),
            new BigDecimal("6000"),
            behaviour);
    }

    @Mock
    private EconomicalSubject economicalSubject;

    @Mock
    private World world;

    @Mock
    private Bank bank;

    @Mock
    private Company company;

    private static final String IBAN = "2";

    private TriggeredWhenBalanceBetweenAThreshold triggeredWhenBalanceBetweenAThreshold;

    @Mock
    private Behaviour behaviour;
}
