package io.wasupu.boinet.persons.behaviours;

import io.wasupu.boinet.ProductType;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MonthlyRecurrentPaymentTest extends RecurrentPaymentTest {

    @Test
    public void shouldPayOnDay25th() {
        when(getWorld().getCurrentDateTime()).thenReturn(fixedDateTime.withDayOfMonth(25));
        when(getPerson().getPan()).thenReturn(getPAN());

        monthlyRecurrentPayment.tick();

        verify(getCompany(), atLeastOnce()).buyProduct(eq(getPAN()), eq(ProductType.ELECTRICITY), getPricesCaptor().capture());
        assertTrue("The 25 tick must pay electricity",
            priceBetween(getLastRecordedPrice(getPricesCaptor().getAllValues()), new BigDecimal(60), new BigDecimal(120)));
    }

    @Test
    public void shouldNotPayOnDifferentDayThan25th() {
        IntStream.range(1, 28)
            .filter(day -> day != 25)
            .forEach(dayOfMonth -> {
                when(getWorld().getCurrentDateTime()).thenReturn(fixedDateTime.withDayOfMonth(dayOfMonth));
                monthlyRecurrentPayment.tick();
            });

        verify(getCompany(), never()).buyProduct(eq(getPAN()), eq(ProductType.ELECTRICITY), any());
    }

    @Test
    public void shouldPayAlwaysTheSamePrice() {
        BigDecimal fixedPrice = new BigDecimal("300");
        int paymentDay = 15;
        MonthlyRecurrentPayment monthlyRecurrentPayment = new MonthlyRecurrentPayment(getWorld(),
            getPerson(),
            paymentDay,
            ProductType.MORTGAGE,
            fixedPrice,
            getCompany());

        when(getPerson().getPan()).thenReturn(getPAN());
        when(getWorld().getCurrentDateTime()).thenReturn(fixedDateTime.withDayOfMonth(paymentDay));

        monthlyRecurrentPayment.tick();
        monthlyRecurrentPayment.tick();

        verify(getCompany(), times(2)).buyProduct(getPAN(), ProductType.MORTGAGE, fixedPrice);
    }

    @Before
    public void setupMonthlyRecurrentPayment() {
        monthlyRecurrentPayment = new MonthlyRecurrentPayment(getWorld(),
            getPerson(),
            25,
            ProductType.ELECTRICITY,
            60,
            120,
            getCompany());
    }

    private MonthlyRecurrentPayment monthlyRecurrentPayment;

    private DateTime fixedDateTime = new DateTime(2017, 1, 1, 1, 1);
}
