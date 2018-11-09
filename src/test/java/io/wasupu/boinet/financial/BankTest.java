package io.wasupu.boinet.financial;

import io.wasupu.boinet.eventPublisher.EventPublisher;
import io.wasupu.boinet.World;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Bank.class)
public class BankTest {

    @Test
    public void it_should_contract_a_new_account() {
        var iban = bank.contractAccount(IDENTIFIER);

        assertEquals("The iban must be 0", IBAN, iban);
        assertTrue("The bank must have the expected firstAccount", bank.existAccount(IBAN));
    }

    @Test
    public void it_should_contract_debit_card_in_the_bank() {
        bank.contractAccount(IDENTIFIER);

        var pan = bank.contractDebitCard(IDENTIFIER, IBAN);

        assertEquals("The pan must be 0", PAN, pan);
        assertEquals("The iban must for pan 0 must be 0", "0", bank.getIbanByPan(PAN));
    }

    @Test
    public void it_should_deposit_money_in_the_bank() {
        bank.contractAccount(IDENTIFIER);

        bank.deposit(IBAN, new BigDecimal(10));

        verify(firstAccount).deposit(new BigDecimal(10));
    }

    @Test
    public void it_should_transfer_money_between_to_accounts() {
        when(firstAccount.getBalance()).thenReturn(new BigDecimal("30"));

        var firstIban = bank.contractAccount(IDENTIFIER);
        var secondIban = bank.contractAccount(OTHER_IDENTIFIER);

        var amount = new BigDecimal(10);
        bank.transfer(firstIban, secondIban, amount);

        verify(firstAccount).withdraw(amount);
        verify(secondAccount).deposit(amount);
    }

    @Test
    public void it_should_not_red_numbers_when_transfer_money_between_to_accounts() {
        when(firstAccount.getBalance()).thenReturn(new BigDecimal("5"));

        var firstIban = bank.contractAccount(IDENTIFIER);
        var secondIban = bank.contractAccount(OTHER_IDENTIFIER);

        var amount = new BigDecimal(10);
        bank.transfer(firstIban, secondIban, amount);

        verify(firstAccount, never()).withdraw(amount);
        verify(secondAccount, never()).deposit(amount);
    }

    @Test
    public void it_should_process_a_payment() {
        when(firstAccount.getBalance()).thenReturn(new BigDecimal("30"));
        var firstIban = bank.contractAccount(IDENTIFIER);
        var secondIban = bank.contractAccount(OTHER_IDENTIFIER);
        var pan = bank.contractDebitCard(IDENTIFIER, firstIban);

        var amount = new BigDecimal("10");

        bank.processCardPayment(amount, pan, secondIban, COMPANY, DETAILS, coordinates);

        verify(secondAccount).deposit(amount);
    }

    @Test
    public void it_should_not_allow_red_number_when_process_a_payment() {
        when(firstAccount.getBalance()).thenReturn(new BigDecimal("3"));

        var firstIban = bank.contractAccount(IDENTIFIER);
        var secondIban = bank.contractAccount(OTHER_IDENTIFIER);
        var pan = bank.contractDebitCard(IDENTIFIER, firstIban);

        var amount = new BigDecimal("10");

        bank.processCardPayment(amount, pan, secondIban, COMPANY, DETAILS, coordinates);

        verify(secondAccount, never()).deposit(any());
    }

    @Test
    public void it_should_publish_an_event_when_contract_an_account() {
        bank.contractAccount(IDENTIFIER);

        verify(eventPublisher).publish(Map.of(
            "eventType", "newAccount",
            "iban", IBAN,
            "user", IDENTIFIER));
    }

    @Test
    public void it_should_publish_an_event_when_contract_a_debit_card() {
        bank.contractDebitCard(IDENTIFIER, IBAN);

        verify(eventPublisher).publish(Map.of(
            "eventType", "newDebitCard",
            "iban", IBAN,
            "pan", PAN,
            "user", IDENTIFIER));
    }

    @Test
    public void it_should_publish_an_event_when_deposit_money_in_card() {
        bank.contractAccount(IDENTIFIER);
        bank.deposit(IBAN, new BigDecimal(10));

        verify(eventPublisher, atLeastOnce()).publish(Map.of(
            "eventType", "deposit",
            "iban", IBAN,
            "amount", new BigDecimal(10),
            "currency", "EUR"));
    }

    @Before
    public void setupAccount() throws Exception {
        whenNew(Account.class).withArguments(IBAN).thenReturn(firstAccount);
        whenNew(Account.class).withArguments(SECOND_IBAN).thenReturn(secondAccount);
    }

    @Before
    public void setupBank() {
        bank = new Bank(world);
        when(world.getCurrentDateTime()).thenReturn(new DateTime(new GregorianCalendar(2017, 10, 10).getTime()));
    }

    @Before
    public void setupEventPublisher() {
        when(world.getEvenPublisher()).thenReturn(eventPublisher);
    }

    private static final String IBAN = "0";

    private static final String SECOND_IBAN = "1";

    private static final String PAN = "0";

    private static final String COMPANY = "12";

    private Pair<Double, Double> coordinates = Pair.of(40.34, -3.4);

    private Bank bank;

    @Mock
    private Account firstAccount;

    @Mock
    private Account secondAccount;

    @Mock
    private World world;

    @Mock
    private EventPublisher eventPublisher;

    private static final String DETAILS = "meal";

    private static final String IDENTIFIER = "1234567";
    private static final String OTHER_IDENTIFIER = "321343";
}
