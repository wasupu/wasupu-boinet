package io.wasupu.boinet.population.behaviours;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WeeklyRecurrentPaymentTest extends RecurrentPaymentTest {

    @Test
    public void shouldPayOnDayInWeekend() {
        when(getWorld().findCompany()).thenReturn(getCompany());
        when(getWorld().getCurrentDateTime()).thenReturn(new DateTime().withDayOfWeek(6));
        when(getPerson().getPan()).thenReturn(getPAN());

        weekendRecurrentPayment.tick();

        verify(getCompany(), atLeastOnce()).buyProduct(eq(getPAN()), eq(ProductType.ENTERTAINMENT), getPricesCaptor().capture());
        assertTrue("The saturday must pay electricity",
            priceBetween(getLastRecordedPrice(getPricesCaptor().getAllValues()), new BigDecimal(60), new BigDecimal(120)));
    }

    @Test
    public void shouldNotPayOnDifferentDayThan6() {
        IntStream.range(1, 7)
            .filter(day -> day != 6 && day != 7)
            .forEach(dayOfWeek -> {
                when(getWorld().getCurrentDateTime()).thenReturn(new DateTime().withDayOfWeek(dayOfWeek));

                weekendRecurrentPayment.tick();
            });

        verify(getCompany(), never()).buyProduct(any(), any(), any());
    }

    @Before
    public void setupMonthlyRecurrentPayment() {
        weekendRecurrentPayment = new WeeklyRecurrentPayment(getWorld(),
            getPerson(),
            ProductType.ENTERTAINMENT,
            60,
            120, 6);
    }

    private WeeklyRecurrentPayment weekendRecurrentPayment;
}
