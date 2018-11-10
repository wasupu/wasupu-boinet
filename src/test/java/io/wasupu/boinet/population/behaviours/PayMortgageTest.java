package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.companies.Company;
import io.wasupu.boinet.companies.ProductType;
import io.wasupu.boinet.financial.Bank;
import io.wasupu.boinet.population.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PayMortgageTest {

    @Test
    public void it_should_pay_a_mortgage() {
        var payment = new PayMortgage(getWorld(),
            getPerson(),
            new BigDecimal(60));

        when(getPerson().getMortgageIdentifier()).thenReturn(MORTGAGE_IDENTIFIER);
        when(world.getBank()).thenReturn(bank);

        payment.tick();

        verify(bank, atLeastOnce()).payMortgage(MORTGAGE_IDENTIFIER, new BigDecimal(60));
    }

    private Person getPerson() {
        return person;
    }

    private World getWorld() {
        return world;
    }

    @Mock
    private Person person;

    @Mock
    private World world;

    @Mock
    private Bank bank;

    private static String MORTGAGE_IDENTIFIER = "0";


}
