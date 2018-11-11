package io.wasupu.boinet.financial;

import io.wasupu.boinet.World;
import io.wasupu.boinet.eventPublisher.EventPublisher;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MortgageTest {

    @Test
    public void it_should_amortize_amount() {
        var mortgage = new Mortgage(MORTGAGE_IDENTIFIER, USER_IDENTIFIER, new BigDecimal(2300), IBAN, world);
        var amount = new BigDecimal(10);

        mortgage.amortize(amount);

        assertEquals("The amortized capital is not the expected", amount, mortgage.getAmortizedAmount());
    }

    @Test
    public void it_should_publish_an_event_when_amortize_amount() {
        var mortgage = new Mortgage(MORTGAGE_IDENTIFIER, USER_IDENTIFIER, new BigDecimal(2300), IBAN, world);
        var amount = new BigDecimal(10);

        mortgage.amortize(amount);

        verify(eventPublisher, atLeastOnce()).publish(Map.of(
            "eventType", "mortgageAmortization",
            "mortgageIdentifier", MORTGAGE_IDENTIFIER,
            "iban", IBAN,
            "originalAmount", new BigDecimal(2300),
            "originalAmount.currency", "EUR",
            "amortizedAmount", new BigDecimal(10),
            "amortizedAmount.currency", "EUR",
            "totalAmortizedAmount", new BigDecimal(10),
            "totalAmortizedAmount.currency", "EUR",
            "date", CURRENT_DATE.toDate()));
    }

    @Test
    public void it_should_be_not_be_amortized_when_create() {
        var mortgage = new Mortgage(MORTGAGE_IDENTIFIER, USER_IDENTIFIER, new BigDecimal(2300), IBAN, world);
        var amount = new BigDecimal(2300);

        mortgage.amortize(amount);

        assertTrue("When original capital is pay the mortgage is amortized", mortgage.isAmortized());
    }

    @Test
    public void it_should_be_be_amortized_when_original_and_amortized_capital_are_the_same() {
        var mortgage = new Mortgage(MORTGAGE_IDENTIFIER, USER_IDENTIFIER, new BigDecimal(2300), IBAN, world);


        assertFalse("When create the mortgate is not amortized", mortgage.isAmortized());
    }

    @Before
    public void setupWorld() {
        when(world.getEvenPublisher()).thenReturn(eventPublisher);
        when(world.getCurrentDateTime()).thenReturn(CURRENT_DATE);
    }

    private static String MORTGAGE_IDENTIFIER = "0";

    private static String USER_IDENTIFIER = "012";

    private static String IBAN = "1";

    private static final DateTime CURRENT_DATE = new DateTime(new GregorianCalendar(2017, 10, 10));

    @Mock
    private World world;

    @Mock
    private EventPublisher eventPublisher;

}
