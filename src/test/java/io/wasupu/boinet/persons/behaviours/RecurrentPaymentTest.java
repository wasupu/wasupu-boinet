package io.wasupu.boinet.persons.behaviours;

import io.wasupu.boinet.Company;
import io.wasupu.boinet.World;
import io.wasupu.boinet.persons.Person;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.List;

public class RecurrentPaymentTest {

    boolean priceBetween(BigDecimal bigDecimal, BigDecimal begin, BigDecimal end) {
        return bigDecimal.compareTo(begin) >= 0 && bigDecimal.compareTo(end) <= 0;
    }

    BigDecimal getLastRecordedPrice(List<BigDecimal> prices) {
        return prices.get(pricesCaptor.getAllValues().size() - 1);
    }

    public ArgumentCaptor<BigDecimal> getPricesCaptor() {
        return pricesCaptor;
    }

    public static String getPAN() {
        return PAN;
    }

    public Company getCompany() {
        return company;
    }

    @Captor
    private ArgumentCaptor<BigDecimal> pricesCaptor;

    public Person getPerson() {
        return person;
    }

    public World getWorld() {
        return world;
    }

    @Mock
    private Person person;

    @Mock
    private World world;

    @Mock
    private Company company;

    private static final String PAN = "12312312312";

}
