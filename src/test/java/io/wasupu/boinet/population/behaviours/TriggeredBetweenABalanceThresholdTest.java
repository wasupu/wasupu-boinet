package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.Bank;
import io.wasupu.boinet.Company;
import io.wasupu.boinet.ProductType;
import io.wasupu.boinet.World;
import io.wasupu.boinet.population.Person;
import io.wasupu.boinet.population.behaviours.balance.TriggeredBetweenABalanceThreshold;
import io.wasupu.boinet.population.behaviours.recurrent.WeeklyRecurrentPayment;
import org.joda.time.DateTime;
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
public class TriggeredBetweenABalanceThresholdTest {

    @Test
    public void shouldNotGoToTheCountrysideIfNotWeekendsWhenIHaveMoreThan6000Euro() {
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal("6001"));
        when(world.getCurrentDateTime()).thenReturn(new DateTime().withDayOfWeek(3));

        triggeredBetweenABalanceThreshold.tick();

        verify(company, never()).buyProduct(any(), any(), any());
    }

    @Test
    public void shouldStartGoingToTheCountrysideOnWeekendsWhenIHaveMoreThan6000Euro() {
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal("6001"));
        when(world.getCurrentDateTime()).thenReturn(new DateTime().withDayOfWeek(6));

        triggeredBetweenABalanceThreshold.tick();

        verify(company, atLeastOnce()).buyProduct(eq(PAN), eq(ProductType.ENTERTAINMENT), pricesCaptor.capture());
        assertTrue("Go to countryside must cost between 100 and 500 euro",
            priceBetween(getLastRecordedPrice(pricesCaptor.getAllValues()), new BigDecimal(100), new BigDecimal(500)));
    }

    @Test
    public void shouldNotGoToTheCountrysideOnWeekendsWhenIHave3000Euro() {
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal("1000"));

        triggeredBetweenABalanceThreshold.tick();

        verify(company, never()).buyProduct(eq(PAN), eq(ProductType.ENTERTAINMENT), any());
    }

    @Test
    public void shouldKeepGoingToTheCountrysideOnWeekendsWhenIHave3000Euro() {
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal("6001"));

        when(world.getCurrentDateTime()).thenReturn(new DateTime().withDayOfWeek(6));

        triggeredBetweenABalanceThreshold.tick();

        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal("3000"));

        triggeredBetweenABalanceThreshold.tick();

        verify(company, times(2)).buyProduct(eq(PAN), eq(ProductType.ENTERTAINMENT), pricesCaptor.capture());
    }

    @Test
    public void shouldStopGoingToTheCountrysideOnWeekendsWhenIHaveLessOf1000Euro() {
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal("999.99"));

        triggeredBetweenABalanceThreshold.tick();

        verify(company, never()).buyProduct(eq(PAN), eq(ProductType.ENTERTAINMENT), any());
    }

    @Before
    public void setupBank() {
        when(person.getIban()).thenReturn(IBAN);
        when(person.getPan()).thenReturn(PAN);
        when(world.getBank()).thenReturn(bank);
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal(12));
        when(world.findCompany()).thenReturn(company);
    }

    @Before
    public void setupGoToCountryside() {
        triggeredBetweenABalanceThreshold = new TriggeredBetweenABalanceThreshold(world,
            person,
            new BigDecimal("1000"),
            new BigDecimal("6000"),
            new WeeklyRecurrentPayment(world,
                person,
                ProductType.ENTERTAINMENT,
                100,
                500, 6));
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

    private TriggeredBetweenABalanceThreshold triggeredBetweenABalanceThreshold;
}
