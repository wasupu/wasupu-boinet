package io.wasupu.boinet.financial;

import io.wasupu.boinet.World;
import io.wasupu.boinet.companies.Company;
import io.wasupu.boinet.companies.ReceiptType;
import io.wasupu.boinet.economicalSubjects.EconomicalSubject;
import io.wasupu.boinet.eventPublisher.EventPublisher;
import io.wasupu.boinet.population.Person;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.Map;

import static io.wasupu.boinet.economicalSubjects.EconomicalSubjectType.PERSON;
import static io.wasupu.boinet.financial.Money.convertMoneyToJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
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
    public void it_should_accept_a_payment() {
        when(firstAccount.getBalance()).thenReturn(new BigDecimal("30"));
        var firstIban = bank.contractAccount(USER_IDENTIFIER);
        var secondIban = bank.contractAccount(OTHER_USER_IDENTIFIER);
        var pan = bank.contractDebitCard(USER_IDENTIFIER, firstIban);

        var amount = new BigDecimal("10");

        bank.payWithCard(amount, pan, secondIban, COMPANY, DETAILS, coordinates);

        verify(secondAccount).deposit(amount);
    }

    @Test
    public void it_should_publish_event_when_accept_payment() {
        when(firstAccount.getBalance()).thenReturn(new BigDecimal("30"));
        var firstIban = bank.contractAccount(USER_IDENTIFIER);
        var secondIban = bank.contractAccount(OTHER_USER_IDENTIFIER);
        var pan = bank.contractDebitCard(USER_IDENTIFIER, firstIban);

        var amount = new BigDecimal("10");

        bank.payWithCard(amount, pan, secondIban, COMPANY, DETAILS, coordinates);

        verifyPublishedEvent(Map.of(
            "eventType", "acceptPayment",
            "pan", IBAN,
            "amount", convertMoneyToJson(amount),
            "details", DETAILS,
            "geolocation", Map.of(
                "latitude", coordinates.getLeft(),
                "longitude", coordinates.getRight()),
            "company", COMPANY,
            "date", CURRENT_DATE.toDate()));
    }

    @Test
    public void it_should_decline_payment_when_no_funds() {
        when(firstAccount.getBalance()).thenReturn(new BigDecimal("3"));

        var firstIban = bank.contractAccount(USER_IDENTIFIER);
        var secondIban = bank.contractAccount(OTHER_USER_IDENTIFIER);
        var pan = bank.contractDebitCard(USER_IDENTIFIER, firstIban);

        var amount = new BigDecimal("10");

        bank.payWithCard(amount, pan, secondIban, COMPANY, DETAILS, coordinates);

        verify(secondAccount, never()).deposit(any());
    }

    @Test
    public void it_should_publish_event_when_decline_payment() {
        when(firstAccount.getBalance()).thenReturn(new BigDecimal("3"));

        var firstIban = bank.contractAccount(USER_IDENTIFIER);
        var secondIban = bank.contractAccount(OTHER_USER_IDENTIFIER);
        var pan = bank.contractDebitCard(USER_IDENTIFIER, firstIban);

        var amount = new BigDecimal("10");

        bank.payWithCard(amount, pan, secondIban, COMPANY, DETAILS, coordinates);

        verifyPublishedEvent(Map.of(
            "eventType", "declinePayment",
            "pan", IBAN,
            "amount", convertMoneyToJson(amount),
            "details", DETAILS,
            "geolocation", Map.of(
                "latitude", coordinates.getLeft(),
                "longitude", coordinates.getRight()),
            "company", COMPANY,
            "date", CURRENT_DATE.toDate()));
    }

    @Test
    public void it_should_contract_a_mortgage_in_the_bank() {
        bank.contractAccount(USER_IDENTIFIER);

        var mortgageIdentifier = bank.contractMortgage(USER_IDENTIFIER, IBAN, MORTGAGE_AMOUNT);

        assertEquals("The mortgage identifier must be 0", MORTGAGE_IDENTIFIER, mortgageIdentifier);
    }

    @Test
    public void it_should_pay_mortgage_installment() {
        when(firstAccount.getBalance()).thenReturn(new BigDecimal("30"));
        when(mortgage.getIban()).thenReturn(IBAN);
        when(mortgage.getAmortizedAmount()).thenReturn(new BigDecimal(0));
        when(mortgage.getTotalAmount()).thenReturn(MORTGAGE_AMOUNT);

        var firstIban = bank.contractAccount(USER_IDENTIFIER);
        var mortgageId = bank.contractMortgage(USER_IDENTIFIER, firstIban, MORTGAGE_AMOUNT);

        var installmentAmount = new BigDecimal("10");

        bank.payMortgage(mortgageId, installmentAmount);

        verify(firstAccount).withdraw(installmentAmount);
        verify(mortgage).amortize(installmentAmount);
    }

    @Test
    public void it_should_pay_remaining_amount_when_installment_amount_is_greater_than_the_rest_of_mortgage() {
        when(firstAccount.getBalance()).thenReturn(new BigDecimal("3000"));
        when(mortgage.getIban()).thenReturn(IBAN);
        when(mortgage.getAmortizedAmount()).thenReturn(new BigDecimal(0));
        when(mortgage.getTotalAmount()).thenReturn(MORTGAGE_AMOUNT);

        var firstIban = bank.contractAccount(USER_IDENTIFIER);
        var mortgageId = bank.contractMortgage(USER_IDENTIFIER, firstIban, MORTGAGE_AMOUNT);

        var amount = new BigDecimal("2000");

        bank.payMortgage(mortgageId, amount);

        verify(firstAccount).withdraw(new BigDecimal(1000));
        verify(mortgage).amortize(new BigDecimal(1000));
    }

    @Test
    public void it_should_not_pay_mortgage_installment_when_no_funds() {
        when(firstAccount.getBalance()).thenReturn(new BigDecimal("50"));
        when(mortgage.getIban()).thenReturn(IBAN);
        when(mortgage.getIdentifier()).thenReturn(MORTGAGE_IDENTIFIER);
        when(mortgage.getAmortizedAmount()).thenReturn(new BigDecimal(0));
        when(mortgage.getTotalAmount()).thenReturn(MORTGAGE_AMOUNT);

        var firstIban = bank.contractAccount(USER_IDENTIFIER);
        var mortgageId = bank.contractMortgage(USER_IDENTIFIER, firstIban, MORTGAGE_AMOUNT);

        var installmentAmount = new BigDecimal("100");

        bank.payMortgage(mortgageId, installmentAmount);

        verify(firstAccount, never()).withdraw(installmentAmount);
        verify(mortgage, never()).amortize(installmentAmount);
    }

    @Test
    public void it_should_publish_event_when_not_paying_mortgage_installment() {
        when(firstAccount.getBalance()).thenReturn(new BigDecimal("50"));
        when(mortgage.getIban()).thenReturn(IBAN);
        when(mortgage.getIdentifier()).thenReturn(MORTGAGE_IDENTIFIER);

        var amortizedAmount = new BigDecimal(0);
        when(mortgage.getAmortizedAmount()).thenReturn(amortizedAmount);
        when(mortgage.getTotalAmount()).thenReturn(MORTGAGE_AMOUNT);

        var firstIban = bank.contractAccount(USER_IDENTIFIER);
        var mortgageId = bank.contractMortgage(USER_IDENTIFIER, firstIban, MORTGAGE_AMOUNT);

        var installmentAmount = new BigDecimal("100");

        bank.payMortgage(mortgageId, installmentAmount);

        verifyPublishedEvent(Map.of(
            "eventType", "declineMortgageInstallment",
            "mortgageIdentifier", mortgageId,
            "iban", IBAN,
            "totalAmount", convertMoneyToJson(MORTGAGE_AMOUNT),
            "installmentAmount", convertMoneyToJson(installmentAmount),
            "totalAmortizedAmount", convertMoneyToJson(amortizedAmount),
            "date", CURRENT_DATE.toDate()));
    }

    @Test
    public void it_should_pay_mortgage_installment_when_enough_funds_for_last_installment() {
        when(firstAccount.getBalance()).thenReturn(new BigDecimal("50"));
        when(mortgage.getIban()).thenReturn(IBAN);
        when(mortgage.getAmortizedAmount()).thenReturn(new BigDecimal("950"));
        when(mortgage.getTotalAmount()).thenReturn(MORTGAGE_AMOUNT);

        var firstIban = bank.contractAccount(USER_IDENTIFIER);
        var mortgageId = bank.contractMortgage(USER_IDENTIFIER, firstIban, MORTGAGE_AMOUNT);

        var installmentAmount = new BigDecimal("100");

        bank.payMortgage(mortgageId, installmentAmount);

        verify(firstAccount).withdraw(new BigDecimal("50"));
        verify(mortgage).amortize(new BigDecimal("50"));
    }

    @Test
    public void it_should_return_true_a_mortgage_is_amortized() {
        var firstIban = bank.contractAccount(USER_IDENTIFIER);
        var mortgageId = bank.contractMortgage(USER_IDENTIFIER, firstIban, MORTGAGE_AMOUNT);
        when(mortgage.isAmortized()).thenReturn(true);

        var amortized = bank.isMortgageAmortized(mortgageId);

        assertTrue("When create a mortage is not amortized", amortized);
    }

    @Test
    public void it_should_cancel_a_mortgage() {
        var firstIban = bank.contractAccount(USER_IDENTIFIER);
        var mortgageId = bank.contractMortgage(USER_IDENTIFIER, firstIban, MORTGAGE_AMOUNT);

        bank.cancelMortgage(mortgageId);

        assertFalse("The mortgage must not exist", bank.existMortgage(mortgageId));
    }

    @Test
    public void it_should_accept_a_receipt() {
        when(firstAccount.getBalance()).thenReturn(new BigDecimal("30"));
        var personIban = bank.contractAccount(USER_IDENTIFIER);
        var companyIban = bank.contractAccount(OTHER_USER_IDENTIFIER);

        when(person.getIban()).thenReturn(personIban);
        when(company.getIban()).thenReturn(companyIban);
        when(company.getIdentifier()).thenReturn(COMPANY);

        var receiptAmount = new BigDecimal("10");

        bank.payReceipt(RECEIPT_ID, ReceiptType.POWER_SUPPLY, receiptAmount, person, company);

        verify(secondAccount).deposit(receiptAmount);
        verify(firstAccount).withdraw(receiptAmount);
    }

    @Test
    public void it_should_publish_an_event_when_accept_a_receipt() {
        when(firstAccount.getBalance()).thenReturn(new BigDecimal("30"));
        var personIban = bank.contractAccount(USER_IDENTIFIER);
        var companyIban = bank.contractAccount(OTHER_USER_IDENTIFIER);

        when(person.getIban()).thenReturn(personIban);
        when(company.getIban()).thenReturn(companyIban);
        when(company.getIdentifier()).thenReturn(COMPANY);

        var receiptAmount = new BigDecimal("10");

        bank.payReceipt(RECEIPT_ID, ReceiptType.POWER_SUPPLY, receiptAmount, person, company);

        verifyPublishedEvent(Map.of(
            "eventType", "acceptReceipt",
            "receiptId", RECEIPT_ID,
            "details", RECEIPT_DETAILS,
            "amount", convertMoneyToJson(receiptAmount),
            "company", COMPANY,
            "date", CURRENT_DATE.toDate()));
    }

    @Test
    public void it_should_publish_an_event_when_contract_an_account() {
        bank.contractAccount(USER_IDENTIFIER);

        verifyPublishedEvent(Map.of(
            "eventType", "contractAccount",
            "iban", IBAN,
            "user", USER_IDENTIFIER,
            "date", CURRENT_DATE.toDate()));
    }

    @Test
    public void it_should_publish_an_event_when_contract_a_debit_card() {
        bank.contractDebitCard(USER_IDENTIFIER, IBAN);

        verifyPublishedEvent(Map.of(
            "eventType", "contractDebitCard",
            "iban", IBAN,
            "pan", PAN,
            "user", USER_IDENTIFIER,
            "date", CURRENT_DATE.toDate()));
    }

    @Test
    public void it_should_publish_an_event_when_contract_an_mortgage() {
        bank.contractMortgage(USER_IDENTIFIER, IBAN, MORTGAGE_AMOUNT);

        verifyPublishedEvent(Map.of(
            "eventType", "contractMortgage",
            "mortgageAmount", convertMoneyToJson(MORTGAGE_AMOUNT),
            "mortgageIdentifier", MORTGAGE_IDENTIFIER,
            "iban", IBAN,
            "user", USER_IDENTIFIER,
            "date", CURRENT_DATE.toDate()));
    }

    @Test
    public void it_should_publish_an_event_when_cancel_an_mortgage() {
        var mortgageIdentifier = bank.contractMortgage(USER_IDENTIFIER, IBAN, MORTGAGE_AMOUNT);
        bank.cancelMortgage(mortgageIdentifier);

        verifyPublishedEvent(Map.of(
            "eventType", "cancelMortgage",
            "mortgageIdentifier", MORTGAGE_IDENTIFIER,
            "iban", IBAN,
            "user", USER_IDENTIFIER,
            "date", CURRENT_DATE.toDate()));
    }

    @Test
    public void it_should_publish_user_registration_event() {
        var subject = mock(EconomicalSubject.class);
        when(subject.getIdentifier()).thenReturn(USER_IDENTIFIER);
        when(subject.getType()).thenReturn(PERSON);

        bank.registerUser(subject);

        verifyPublishedEvent(Map.of(
            "eventType", "registerUser",
            "user", USER_IDENTIFIER,
            "type", PERSON.toString(),
            "date", CURRENT_DATE.toDate()));
    }

    @Before
    public void setupAccount() throws Exception {
        whenNew(Account.class).withArguments(IBAN, world).thenReturn(firstAccount);
        whenNew(Account.class).withArguments(SECOND_IBAN, world).thenReturn(secondAccount);
    }

    @Before
    public void setupMortgage() throws Exception {
        whenNew(Mortgage.class).withArguments("0", USER_IDENTIFIER, MORTGAGE_AMOUNT, IBAN, world).thenReturn(mortgage);
        when(mortgage.getUserIdentifier()).thenReturn(USER_IDENTIFIER);
        when(mortgage.getIban()).thenReturn(IBAN);
    }

    @Before
    public void setupBank() {
        bank = new Bank(world);
    }

    @Before
    public void setupWorld() {
        when(world.getEventPublisher()).thenReturn(eventPublisher);
        when(world.getCurrentDateTime()).thenReturn(CURRENT_DATE);
    }

    private void verifyPublishedEvent(Map<String, Object> expectedEvent) {
        verify(eventPublisher, atLeastOnce()).publish(eventCaptor.capture());

        assertThat(eventCaptor.getAllValues())
            .as("The expected event has not been published")
            .contains(expectedEvent);
    }

    private static final String IBAN = "0";

    private static final String SECOND_IBAN = "1";

    private static final String PAN = "0";

    private static final String MORTGAGE_IDENTIFIER = "0";

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

    @Mock
    private Person person;

    @Mock
    private Company company;

    @Captor
    private ArgumentCaptor<Map<String, Object>> eventCaptor;

    private static final String DETAILS = "meal";

    private static final String RECEIPT_DETAILS = "power_supply";

    private static final String USER_IDENTIFIER = "1234567";
    private static final String OTHER_USER_IDENTIFIER = "321343";

    private static final DateTime CURRENT_DATE = new DateTime(new GregorianCalendar(2017, 10, 10));

    private BigDecimal MORTGAGE_AMOUNT = new BigDecimal(1000);

    private String RECEIPT_ID = "98767";
}
