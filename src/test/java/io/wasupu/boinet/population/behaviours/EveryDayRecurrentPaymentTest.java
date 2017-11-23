package io.wasupu.boinet.population.behaviours;


import io.wasupu.boinet.ProductType;
import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EveryDayRecurrentPaymentTest extends RecurrentPaymentTest {

    @Test
    public void shouldEatEveryTickAfterTwoTicks() {
        when(getPerson().getAge()).thenReturn(2L);
        when(getWorld().findCompany()).thenReturn(getCompany());
        when(getPerson().getPan()).thenReturn(getPAN());

        eatEveryDay.tick();
        eatEveryDay.tick();
        eatEveryDay.tick();

        verify(getCompany(), times(3)).buyProduct(eq(getPAN()), eq(ProductType.MEAL), getPricesCaptor().capture());

        assertThat(getPricesCaptor().getAllValues())
            .as("There must be 3 random values between 10 and 20 euros")
            .isNotEmpty()
            .hasSize(3)
            .are(new Condition<>(bigDecimal -> priceBetween(bigDecimal,
                new BigDecimal(10),
                new BigDecimal(20)), "More than ten, less than twenty"));
    }

    @Before
    public void setUpEveryDayRecurrentPayment() {
        eatEveryDay = new EveryDayRecurrentPayment(getWorld(), getPerson(), ProductType.MEAL, 10, 20);
    }

    private EveryDayRecurrentPayment eatEveryDay;
}
