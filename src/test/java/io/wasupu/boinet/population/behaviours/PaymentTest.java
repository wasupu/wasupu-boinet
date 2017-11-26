package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.Company;
import io.wasupu.boinet.ProductType;
import io.wasupu.boinet.World;
import io.wasupu.boinet.population.Person;
import io.wasupu.boinet.population.behaviours.Payment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PaymentTest {

    @Test
    public void shouldMakeAPaymentWithPriceRange() {
        Payment payment = new Payment(getWorld(),
            getPerson(),
            ProductType.ENTERTAINMENT,
            60,
            120);

        when(getWorld().findCompany()).thenReturn(getCompany());
        when(getPerson().getPan()).thenReturn(getPAN());

        payment.tick();

        verify(getCompany(), atLeastOnce()).buyProduct(eq(getPAN()), eq(ProductType.ENTERTAINMENT), getPricesCaptor().capture());
        assertTrue("Must make the payment in the price range",
            priceBetween(getLastRecordedPrice(getPricesCaptor().getAllValues()), new BigDecimal(60), new BigDecimal(120)));
    }

    @Test
    public void shouldMakeAPaymentWithFixedPrice() {
        Payment payment = new Payment(getWorld(),
            getPerson(),
            ProductType.ENTERTAINMENT,
            new BigDecimal(60),
            company
        );

        when(getPerson().getPan()).thenReturn(getPAN());

        payment.tick();

        verify(company, atLeastOnce()).buyProduct(eq(getPAN()), eq(ProductType.ENTERTAINMENT), eq(new BigDecimal(60)));

    }

    private boolean priceBetween(BigDecimal bigDecimal, BigDecimal begin, BigDecimal end) {
        return bigDecimal.compareTo(begin) >= 0 && bigDecimal.compareTo(end) <= 0;
    }

    private BigDecimal getLastRecordedPrice(List<BigDecimal> prices) {
        return prices.get(pricesCaptor.getAllValues().size() - 1);
    }

    private  ArgumentCaptor<BigDecimal> getPricesCaptor() {
        return pricesCaptor;
    }

    private static String getPAN() {
        return PAN;
    }

    private Company getCompany() {
        return company;
    }

    private Person getPerson() {
        return person;
    }

    private World getWorld() {
        return world;
    }

    @Captor
    private ArgumentCaptor<BigDecimal> pricesCaptor;

    @Mock
    private Person person;

    @Mock
    private World world;

    @Mock
    private Company company;

    private static final String PAN = "12312312312";

}
