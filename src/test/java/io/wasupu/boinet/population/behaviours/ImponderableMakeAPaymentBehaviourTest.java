package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.companies.Company;
import io.wasupu.boinet.companies.ProductType;
import io.wasupu.boinet.population.Person;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ImponderablePaymentBehaviour.class)
public class ImponderableMakeAPaymentBehaviourTest {

    @Test
    public void shouldNotPayTheImponderableIfNotOccurs() {
        when(random.nextDouble()).thenReturn(0.2);

        imponderablePaymentBehaviour.tick();

        verify(getCompany(), never()).buyProduct(eq(getPAN()), eq(ProductType.CAR_FAULT), pricesCaptor.capture());
    }

    @Test
    public void shouldOccursOnlyInAProbability() {
        when(getWorld().findCompany()).thenReturn(getCompany());
        when(getPerson().getPan()).thenReturn(getPAN());
        when(random.nextDouble()).thenReturn(0.01);

        imponderablePaymentBehaviour.tick();

        verify(getCompany(), atLeastOnce()).buyProduct(eq(getPAN()), eq(ProductType.CAR_FAULT), pricesCaptor.capture());

        assertTrue("Must pay the car fault",
            priceBetween(getLastRecordedPrice(pricesCaptor.getAllValues()), new BigDecimal(100), new BigDecimal(1000)));
    }


    private BigDecimal getLastRecordedPrice(List<BigDecimal> prices) {
        return prices.get(pricesCaptor.getAllValues().size() - 1);
    }

    private boolean priceBetween(BigDecimal bigDecimal, BigDecimal begin, BigDecimal end) {
        return bigDecimal.compareTo(begin) >= 0 && bigDecimal.compareTo(end) <= 0;
    }


    @Before
    public void setImponderablePaymentBehaviour() throws Exception {
        whenNew(Random.class).withAnyArguments().thenReturn(random);
        imponderablePaymentBehaviour = new ImponderablePaymentBehaviour(getWorld(),
            getPerson(),
            ProductType.CAR_FAULT, 100, 1000, 3.0);
    }

    private ImponderablePaymentBehaviour imponderablePaymentBehaviour;

    public Person getPerson() {
        return person;
    }

    public World getWorld() {
        return world;
    }

    public Company getCompany() {
        return company;
    }

    private static String getPAN() {
        return PAN;
    }

    @Mock
    private Person person;

    @Mock
    private World world;

    @Mock
    private Company company;

    private static final String PAN = "12312312312";

    @Captor
    private ArgumentCaptor<BigDecimal> pricesCaptor;

    @Mock
    private Random random;
}
