package io.wasupu.boinet.economicalSubjects.behaviours.balance;

import io.wasupu.boinet.financial.Bank;
import io.wasupu.boinet.companies.Company;
import io.wasupu.boinet.World;
import io.wasupu.boinet.population.Person;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TriggeredWhenBalanceIsBetweenAThresholdTest {

    @Test
    public void shouldNotGoToTheCountrysideIfNotWeekendsWhenIHaveMoreThan6000Euro() {
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal("6001"));

        triggeredWhenBalanceBetweenAThreshold.tick();

        verify(company, never()).buyProduct(any(), any(), any());
    }

    @Test
    public void shouldStartGoingToTheCountrysideOnWeekendsWhenIHaveMoreThan6000Euro() {
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal("6001"));

        triggeredWhenBalanceBetweenAThreshold.tick();

        verify(personBehaviour, atLeastOnce()).tick();
    }

    @Test
    public void shouldNotGoToTheCountrysideOnWeekendsWhenIHave3000Euro() {
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal("1000"));

        triggeredWhenBalanceBetweenAThreshold.tick();

        verify(personBehaviour, never()).tick();
    }

    @Test
    public void shouldKeepGoingToTheCountrysideOnWeekendsWhenIHave3000Euro() {
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal("6001"));

        triggeredWhenBalanceBetweenAThreshold.tick();

        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal("3000"));

        triggeredWhenBalanceBetweenAThreshold.tick();

        verify(personBehaviour, times(2)).tick();
    }

    @Test
    public void shouldStopGoingToTheCountrysideOnWeekendsWhenIHaveLessOf1000Euro() {
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal("999.99"));

        triggeredWhenBalanceBetweenAThreshold.tick();

        verify(personBehaviour, never()).tick();
    }

    @Before
    public void setupBank() {
        when(person.getIban()).thenReturn(IBAN);
        when(world.getBank()).thenReturn(bank);
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal(12));
    }

    @Before
    public void setupGoToCountryside() {
        triggeredWhenBalanceBetweenAThreshold = new TriggeredWhenBalanceBetweenAThreshold(world,
            person,
            new BigDecimal("1000"),
            new BigDecimal("6000"),
            personBehaviour);
    }

    private boolean priceBetween(BigDecimal bigDecimal, BigDecimal begin, BigDecimal end) {
        return bigDecimal.compareTo(begin) >= 0 && bigDecimal.compareTo(end) <= 0;
    }

    private BigDecimal getLastRecordedPrice(List<BigDecimal> prices) {
        return prices.get(pricesCaptor.getAllValues().size() - 1);
    }

    @Mock
    private Person person;

    @Mock
    private World world;

    @Mock
    private Bank bank;

    @Mock
    private Company company;

    private static final String PAN = "12312312312";

    private static final String IBAN = "2";

    @Captor
    private ArgumentCaptor<BigDecimal> pricesCaptor;

    private TriggeredWhenBalanceBetweenAThreshold triggeredWhenBalanceBetweenAThreshold;

    @Mock
    private EconomicalSubjectBehaviour personBehaviour;
}
