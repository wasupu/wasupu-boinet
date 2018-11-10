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
        var iban = bank.contractAccount(USER_IDENTIFIER);

        assertEquals("The iban must be 0", IBAN, iban);
        assertTrue("The bank must have the expected firstAccount", bank.existAccount(IBAN));
    }

    @Test
    public void it_should_contract_debit_card_in_the_bank() {
        bank.contractAccount(USER_IDENTIFIER);

        var pan = bank.contractDebitCard(USER_IDENTIFIER, IBAN);

        assertEquals("The pan must be 0", PAN, pan);
        assertEquals("The iban must be 0 for pan 0", "0", bank.getIbanByPan(PAN));
    }

    @Test
    public void it_should_contract_a_mortgage_in_the_bank() {
        bank.contractAccount(USER_IDENTIFIER);

        var mortgageIdentifier = bank.contractMortgage(USER_IDENTIFIER, IBAN, MORTGATE_AMOUNT);

        assertEquals("The mortgage identifier must be 0", MORTGAGE_IDENTFIER, mortgageIdentifier);
    }

    @Test
    public void it_should_deposit_money_in_the_bank() {
        bank.contractAccount(USER_IDENTIFIER);
        when(firstAccount.getBalance()).thenReturn(new BigDecimal(10));

        bank.deposit(IBAN, new BigDecimal(10));

        verify(firstAccount).deposit(new BigDecimal(10));
    }

    @Test
    public void it_should_transfer_money_between_to_accounts() {
        when(firstAccount.getBalance()).thenReturn(new BigDecimal("30"));

        var firstIban = bank.contractAccount(USER_IDENTIFIER);
        var secondIban = bank.contractAccount(OTHER_USER_IDENTIFIER);

        var amount = new BigDecimal(10);
        bank.transfer(firstIban, secondIban, amount);

        verify(firstAccount).withdraw(amount);
        verify(secondAccount).deposit(amount);
    }

    @Test
    public void it_should_not_red_numbers_when_transfer_money_between_to_accounts() {
        when(firstAccount.getBalance()).thenReturn(new BigDecimal("5"));

        var firstIban = bank.contractAccount(USER_IDENTIFIER);
        var secondIban = bank.contractAccount(OTHER_USER_IDENTIFIER);

        var amount = new BigDecimal(10);
        bank.transfer(firstIban, secondIban, amount);

        verify(firstAccount, never()).withdraw(amount);
        verify(secondAccount, never()).deposit(amount);
    }

    @Test
    public void it_should_process_a_payment() {
        when(firstAccount.getBalance()).thenReturn(new BigDecimal("30"));
        var firstIban = bank.contractAccount(USER_IDENTIFIER);
        var secondIban = bank.contractAccount(OTHER_USER_IDENTIFIER);
        var pan = bank.contractDebitCard(USER_IDENTIFIER, firstIban);

        var amount = new BigDecimal("10");

        bank.processCardPayment(amount, pan, secondIban, COMPANY, DETAILS, coordinates);

        verify(secondAccount).deposit(amount);
    }

    @Test
    public void it_should_process_a_mortgage_payment() {
        when(firstAccount.getBalance()).thenReturn(new BigDecimal("30"));
        when(mortgage.getIban()).thenReturn(IBAN);

        var firstIban = bank.contractAccount(USER_IDENTIFIER);
        var mortgageId = bank.contractMortgage(USER_IDENTIFIER, firstIban, MORTGATE_AMOUNT);

        var amount = new BigDecimal("10");

        bank.payMortgage(mortgageId, amount);

        verify(firstAccount).withdraw(amount);
        verify(mortgage).amortize(amount);
    }

    @Test
    public void it_should_not_allow_red_number_when_process_a_payment() {
        when(firstAccount.getBalance()).thenReturn(new BigDecimal("3"));

        var firstIban = bank.contractAccount(USER_IDENTIFIER);
        var secondIban = bank.contractAccount(OTHER_USER_IDENTIFIER);
        var pan = bank.contractDebitCard(USER_IDENTIFIER, firstIban);

        var amount = new BigDecimal("10");

        bank.processCardPayment(amount, pan, secondIban, COMPANY, DETAILS, coordinates);

        verify(secondAccount, never()).deposit(any());
    }

    @Test
    public void it_should_publish_an_event_when_contract_an_account() {
        bank.contractAccount(USER_IDENTIFIER);

        verify(eventPublisher).publish(Map.of(
            "eventType", "newAccount",
            "iban", IBAN,
            "user", USER_IDENTIFIER,
            "date", CURRENT_DATE.toDate()));
    }

    @Test
    public void it_should_publish_an_event_when_contract_a_debit_card() {
        bank.contractDebitCard(USER_IDENTIFIER, IBAN);

        verify(eventPublisher).publish(Map.of(
            "eventType", "newDebitCard",
            "iban", IBAN,
            "pan", PAN,
            "user", USER_IDENTIFIER,
            "date", CURRENT_DATE.toDate()));
    }

    @Test
    public void it_should_publish_an_event_when_contract_an_mortgage() {
        bank.contractMortgage(USER_IDENTIFIER, IBAN, MORTGATE_AMOUNT);

        verify(eventPublisher).publish(Map.of(
            "eventType", "newMortgage",
            "mortgageAmount", MORTGATE_AMOUNT,
            "mortgageIdentifier", MORTGAGE_IDENTFIER,
            "iban", IBAN,
            "user", USER_IDENTIFIER,
            "date", CURRENT_DATE.toDate()));
    }

    @Before
    public void setupAccount() throws Exception {
        whenNew(Account.class).withArguments(IBAN, world).thenReturn(firstAccount);
        whenNew(Account.class).withArguments(SECOND_IBAN, world).thenReturn(secondAccount);
    }

    @Before
    public void setupMortgage() throws Exception {
        whenNew(Mortgage.class).withArguments("0", MORTGATE_AMOUNT, IBAN, world).thenReturn(mortgage);
    }

    @Before
    public void setupBank() {
        bank = new Bank(world);
        when(world.getCurrentDateTime()).thenReturn(CURRENT_DATE);
    }

    @Before
    public void setupEventPublisher() {
        when(world.getEvenPublisher()).thenReturn(eventPublisher);
    }

    private static final String IBAN = "0";

    private static final String SECOND_IBAN = "1";

    private static final String PAN = "0";

    private static final String MORTGAGE_IDENTFIER = "0";

    private static final String COMPANY = "12";

    private Pair<Double, Double> coordinates = Pair.of(40.34, -3.4);

    private Bank bank;

    @Mock
    private Account firstAccount;

    @Mock
    private Account secondAccount;

    @Mock
    private Mortgage mortgage;

    @Mock
    private World world;

    @Mock
    private EventPublisher eventPublisher;

    private static final String DETAILS = "meal";

    private static final String USER_IDENTIFIER = "1234567";
    private static final String OTHER_USER_IDENTIFIER = "321343";

    private static final DateTime CURRENT_DATE = new DateTime(new GregorianCalendar(2017, 10, 10));

    private BigDecimal MORTGATE_AMOUNT = new BigDecimal(1000);
}
