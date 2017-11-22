package io.wasupu.boinet.persons.behaviours;

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
import java.util.stream.IntStream;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MonthlyRecurrentPaymentTest {

    @Test
    public void shouldPayElectricityOnDay25th() {
        when(world.findCompany()).thenReturn(company);
        when(world.getCurrentDateTime()).thenReturn(new DateTime().withDayOfMonth(25));
        when(person.getPan()).thenReturn(PAN);

        monthlyRecurrentPayment.tick();

        verify(company, atLeastOnce()).buyProduct(eq(PAN), eq(ProductType.ELECTRICITY), pricesCaptor.capture());
        assertTrue("The 25 tick must pay electricity",
            priceBetween(getLastRecordedPrice(pricesCaptor.getAllValues()), new BigDecimal(60), new BigDecimal(120)));
    }

    @Test
    public void shouldNotPayElectricityOnDifferentDayThan25th() {
        IntStream.range(1, 28)
            .filter(day -> day != 25)
            .forEach(dayOfMonth -> {
                when(world.getCurrentDateTime()).thenReturn(new DateTime().withDayOfMonth(dayOfMonth));
                monthlyRecurrentPayment.tick();
            });

        verify(company, never()).buyProduct(eq(PAN), eq(ProductType.ELECTRICITY), any());
    }

    @Before
    public void setupGoToCountryside() {
        monthlyRecurrentPayment = new MonthlyRecurrentPayment(world,
            person,
            25,
            ProductType.ELECTRICITY,
            60,
            120);
    }

    private boolean priceBetween(BigDecimal bigDecimal, BigDecimal begin, BigDecimal end) {
        return bigDecimal.compareTo(begin) >= 0 && bigDecimal.compareTo(end) <= 0;
    }

    private BigDecimal getLastRecordedPrice(List<BigDecimal> prices) {
        return prices.get(pricesCaptor.getAllValues().size() - 1);
    }

    @Captor
    private ArgumentCaptor<BigDecimal> pricesCaptor;



    private static final String PAN = "12312312312";

    @Mock
    private Person person;

    @Mock
    private World world;

    @Mock
    private Company company;

    private MonthlyRecurrentPayment monthlyRecurrentPayment;
}
