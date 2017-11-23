package io.wasupu.boinet.persons.behaviours;

import io.wasupu.boinet.Bank;
import io.wasupu.boinet.Company;
import io.wasupu.boinet.ProductType;
import io.wasupu.boinet.World;
import io.wasupu.boinet.persons.Person;
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
public class TriggeredByBalanceThresholdTest {

    @Test
    public void shouldNotGoingToTheCountrysideIfNotWeekendsWhenIHaveMoreThan6000Euro() {
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal("6001"));
        when(world.getCurrentDateTime()).thenReturn(new DateTime().withDayOfWeek(3));

        triggeredByBalanceThreshold.tick();

        verify(company, never()).buyProduct(any(), any(), any());
    }

    @Test
    public void shouldStartGoingToTheCountrysideOnWeekendsWhenIHaveMoreThan6000Euro() {
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal("6001"));
        when(world.getCurrentDateTime()).thenReturn(new DateTime().withDayOfWeek(6));

        triggeredByBalanceThreshold.tick();

        verify(company, atLeastOnce()).buyProduct(eq(PAN), eq(ProductType.ENTERTAINMENT), pricesCaptor.capture());
        assertTrue("Go to countryside must cost between 100 and 500 euro",
            priceBetween(getLastRecordedPrice(pricesCaptor.getAllValues()), new BigDecimal(100), new BigDecimal(500)));
    }

    @Test
    public void shouldNotGoToTheCountrysideOnWeekendsWhenIHave3000Euro() {
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal("3000"));

        triggeredByBalanceThreshold.tick();

        verify(company, never()).buyProduct(eq(PAN), eq(ProductType.ENTERTAINMENT), any());
    }

    @Test
    public void shouldKeepGoingToTheCountrysideOnWeekendsWhenIHave3000Euro() {
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal("6001"));

        when(world.getCurrentDateTime()).thenReturn(new DateTime().withDayOfWeek(6));

        triggeredByBalanceThreshold.tick();

        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal("3000"));

        triggeredByBalanceThreshold.tick();

        verify(company, times(2)).buyProduct(eq(PAN), eq(ProductType.ENTERTAINMENT), pricesCaptor.capture());
    }

    @Test
    public void shouldStopGoingToTheCountrysideOnWeekendsWhenIHaveLessOf1000Euro() {
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal("999.99"));

        triggeredByBalanceThreshold.tick();

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
        triggeredByBalanceThreshold = new TriggeredByBalanceThreshold(world,
            person,
            new BigDecimal("1000"),
            new BigDecimal("6000"),
            new WeekendRecurrentPayment(world,
                person,
                ProductType.ENTERTAINMENT,
                100,
                500));
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

    private TriggeredByBalanceThreshold triggeredByBalanceThreshold;
}
