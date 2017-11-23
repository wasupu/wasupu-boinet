package io.wasupu.boinet.persons.behaviours;

import io.wasupu.boinet.Company;
import io.wasupu.boinet.ProductType;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MonthlyRecurrentPaymentTest extends RecurrentPaymentTest{

    @Test
    public void shouldPayOnDay25th() {
        when(getWorld().findCompany()).thenReturn(getCompany());
        when(getWorld().getCurrentDateTime()).thenReturn(new DateTime().withDayOfMonth(25));
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
                when(getWorld().getCurrentDateTime()).thenReturn(new DateTime().withDayOfMonth(dayOfMonth));
                monthlyRecurrentPayment.tick();
            });

        verify(getCompany(), never()).buyProduct(eq(getPAN()), eq(ProductType.ELECTRICITY), any());
    }

    @Before
    public void setupMonthlyRecurrentPayment() {
        monthlyRecurrentPayment = new MonthlyRecurrentPayment(getWorld(),
            getPerson(),
            25,
            ProductType.ELECTRICITY,
            60,
            120);
    }

    private MonthlyRecurrentPayment monthlyRecurrentPayment;
}
