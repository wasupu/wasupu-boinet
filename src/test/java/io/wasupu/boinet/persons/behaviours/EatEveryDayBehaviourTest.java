package io.wasupu.boinet.persons.behaviours;


import io.wasupu.boinet.Company;
import io.wasupu.boinet.ProductType;
import io.wasupu.boinet.World;
import io.wasupu.boinet.persons.Person;
import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EatEveryDayBehaviourTest {
    @Test
    public void shouldEatEveryTickAfterTwoTicks() {
        when(person.getAge()).thenReturn(2L);
        when(world.findCompany()).thenReturn(company);
        when(person.getPan()).thenReturn(PAN);

        eatEveryDay.tick();
        eatEveryDay.tick();
        eatEveryDay.tick();

        verify(company, times(3)).buyProduct(eq(PAN), eq(ProductType.MEAL), pricesCaptor.capture());

        assertThat(pricesCaptor.getAllValues())
            .as("There must be 3 random values between 10 and 20 euros")
            .isNotEmpty()
            .hasSize(3)
            .are(new Condition<>(bigDecimal -> priceBetween(bigDecimal,
                new BigDecimal(10),
                new BigDecimal(20)), "More than ten, less than twenty"));
    }

    private boolean priceBetween(BigDecimal bigDecimal, BigDecimal begin, BigDecimal end) {
        return bigDecimal.compareTo(begin) >= 0 && bigDecimal.compareTo(end) <= 0;
    }

    @Before
    public void setupEatEveryDay() {
        eatEveryDay = new EatEveryDay(world, person);
    }

    private EatEveryDay eatEveryDay;

    @Mock
    private Person person;

    @Mock
    private World world;

    @Captor
    private ArgumentCaptor<BigDecimal> pricesCaptor;

    @Mock
    private Company company;

    private static final String PAN = "12312312312";

}
