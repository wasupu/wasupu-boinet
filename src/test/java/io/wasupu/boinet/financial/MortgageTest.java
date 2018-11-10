package io.wasupu.boinet.financial;

import io.wasupu.boinet.World;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class MortgageTest {

    @Test
    public void it_should_amortize_amount() {
        var mortgage = new Mortgage(MORRGAGE_IDENTIFIER, new BigDecimal(2300), IBAN, world);

        var amount = new BigDecimal(10);

        mortgage.amortize(amount);

        assertEquals("The amortized capital is not the expected", amount, mortgage.getAmortizedAmount());
    }

    private static String MORRGAGE_IDENTIFIER = "0";

    private static String IBAN = "1";


    @Mock
    private World world;


}
