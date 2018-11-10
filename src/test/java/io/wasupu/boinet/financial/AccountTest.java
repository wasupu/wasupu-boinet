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
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AccountTest {

    @Test
    public void it_should_deposit_money_into_account() {
        account.deposit(new BigDecimal(10));

        assertEquals("The balance of the account is not the expected",
            new BigDecimal(10),
            account.getBalance());
    }

    @Test
    public void it_should_withdraw_money_from_account() {
        account.deposit(new BigDecimal(10));
        account.withdraw(new BigDecimal(3));
        assertEquals("The balance of the account is not the expected",
            new BigDecimal(7),
            account.getBalance());
    }

    @Test
    public void it_should_publish_an_event_when_deposit_money() {
        account.deposit(new BigDecimal(10));

        verify(eventPublisher, atLeastOnce()).publish(Map.of(
            "eventType", "deposit",
            "iban", IBAN,
            "amount", new BigDecimal(10),
            "amount.currency", "EUR",
            "balance", new BigDecimal(10),
            "balance.currency", "EUR",
            "date", CURRENT_DATE.toDate()));
    }

    @Test
    public void it_should_publish_an_event_when_withdraw_money() {
        account.deposit(new BigDecimal(100));
        account.withdraw(new BigDecimal(10));

        verify(eventPublisher, atLeastOnce()).publish(Map.of(
            "eventType", "withdraw",
            "iban", IBAN,
            "amount", new BigDecimal(10),
            "amount.currency", "EUR",
            "balance", new BigDecimal(90),
            "balance.currency", "EUR",
            "date", CURRENT_DATE.toDate()));
    }

    @Before
    public void setupAccount() {
        account = new Account(IBAN, world);
        when(world.getCurrentDateTime()).thenReturn(CURRENT_DATE);
    }

    @Before
    public void setupEventPublisher() {
        when(world.getEvenPublisher()).thenReturn(eventPublisher);
    }


    @Mock
    private EventPublisher eventPublisher;

    private static final String IBAN = "12";

    private Account account;

    @Mock
    private World world;

    private static final DateTime CURRENT_DATE = new DateTime(new GregorianCalendar(2017, 10, 10));
}


