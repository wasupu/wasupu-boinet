package io.wasupu.boinet.financial.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.companies.Company;
import io.wasupu.boinet.eventPublisher.EventPublisher;
import io.wasupu.boinet.financial.Account;
import io.wasupu.boinet.financial.Bank;
import io.wasupu.boinet.population.Person;
import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BankFinancialStatusTest {

    @Test
    public void it_should_publish_the_bank_balance_with_event_type_balance() {
        var value = executeBehaviour();

        assertEquals("Must have the balance event type", "bankBalance", value.get("eventType"));
    }

    @Test
    public void it_should_publish_the_bank_balance_the_treasury_account_balance() {
        var value = executeBehaviour();

        assertEquals("Must have the balance of the treasury", TREASURY_BALANCE, value.get("treasuryAccount"));
    }

    @Test
    public void it_should_publish_the_people_balance() {
        when(person.getIban()).thenReturn(IBAN);
        when(otherPerson.getIban()).thenReturn(OTHER_IBAN);

        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal(23));
        when(bank.getBalance(OTHER_IBAN)).thenReturn(new BigDecimal(12));

        when(world.getPopulation()).thenReturn(List.of(person, otherPerson));

        var value = executeBehaviour();

        assertEquals("Must have the people balance", new BigDecimal(35), value.get("peopleBalance"));
    }

    @Test
    public void it_should_publish_the_the_max_people_balance() {
        when(person.getIban()).thenReturn(IBAN);
        when(otherPerson.getIban()).thenReturn(OTHER_IBAN);

        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal(23));
        when(bank.getBalance(OTHER_IBAN)).thenReturn(new BigDecimal(12));

        when(world.getPopulation()).thenReturn(List.of(person, otherPerson));

        var value = executeBehaviour();

        assertEquals("Must have max the people balance", new BigDecimal(23), value.get("maxPeopleBalance"));
    }

    @Test
    public void it_should_publish_the_the_min_people_balance() {
        when(person.getIban()).thenReturn(IBAN);
        when(otherPerson.getIban()).thenReturn(OTHER_IBAN);

        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal(23));
        when(bank.getBalance(OTHER_IBAN)).thenReturn(new BigDecimal(12));

        when(world.getPopulation()).thenReturn(List.of(person, otherPerson));

        var value = executeBehaviour();

        assertEquals("Must have min the people balance", new BigDecimal(12), value.get("minPeopleBalance"));
    }

    @Test
    public void it_should_publish_the_companies_balance() {
        when(company.getIban()).thenReturn(IBAN);
        when(otherCompany.getIban()).thenReturn(OTHER_IBAN);

        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal(22));
        when(bank.getBalance(OTHER_IBAN)).thenReturn(new BigDecimal(32));

        when(world.getCompanies()).thenReturn(List.of(company, otherCompany));

        var value = executeBehaviour();

        assertEquals("Must have the company balance", new BigDecimal(54), value.get("companiesBalance"));
    }

    @Test
    public void it_should_publish_the_the_max_company_balance() {
        when(company.getIban()).thenReturn(IBAN);
        when(otherCompany.getIban()).thenReturn(OTHER_IBAN);

        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal(22));
        when(bank.getBalance(OTHER_IBAN)).thenReturn(new BigDecimal(32));

        when(world.getCompanies()).thenReturn(List.of(company, otherCompany));

        Map<String, Object> value = executeBehaviour();

        assertEquals("Must have max the company balance", new BigDecimal(32), value.get("maxCompaniesBalance"));
    }

    @Test
    public void it_should_publish_the_the_min_company_balance() {
        when(company.getIban()).thenReturn(IBAN);
        when(otherCompany.getIban()).thenReturn(OTHER_IBAN);

        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal(22));
        when(bank.getBalance(OTHER_IBAN)).thenReturn(new BigDecimal(32));

        when(world.getCompanies()).thenReturn(List.of(company, otherCompany));

        var value = executeBehaviour();

        assertEquals("Must have min the company balance", new BigDecimal(22), value.get("minCompaniesBalance"));
    }

    @Test
    public void it_should_publish_the_the_95th_percentile_max_company_balance() {
        var companies = IntStream.range(1, 101)
            .mapToObj(this::createCompanyWithBalance)
            .collect(Collectors.toList());

        when(world.getCompanies()).thenReturn(companies);

        var value = executeBehaviour();

        assertThat(value)
            .as("95th percentile must be published")
            .containsKey("95thPercentileCompaniesBalance")
            .hasEntrySatisfying("95thPercentileCompaniesBalance", balance ->
                assertThat((BigDecimal) balance)
                    .as("95th percentile must be less than 96")
                    .isLessThan(new BigDecimal("96")));
    }

    @Test
    public void it_should_publish_the_the_95th_percentile_max_people_balance() {
        var population = IntStream.range(1, 101)
            .mapToObj(this::createPersonWithBalance)
            .collect(Collectors.toList());

        when(world.getPopulation()).thenReturn(population);

        var event = executeBehaviour();

        assertThat(event)
            .as("95th percentile must be published")
            .containsKey("95thPercentilePopulationBalance")
            .hasEntrySatisfying("95thPercentilePopulationBalance", balance ->
                assertThat((BigDecimal) balance)
                    .as("95th percentile must be less than 96")
                    .isLessThan(new BigDecimal("96")));
    }

    private Person createPersonWithBalance(Integer balance) {
        var iban = "IBAN-" + balance;
        var person = mock(Person.class, iban);

        when(person.getIban()).thenReturn(iban);
        when(bank.getBalance(iban)).thenReturn(new BigDecimal(balance));

        return person;
    }

    private Company createCompanyWithBalance(Integer balance) {
        var iban = "IBAN-" + balance;
        var company = mock(Company.class, iban);

        when(company.getIban()).thenReturn(iban);
        when(bank.getBalance(iban)).thenReturn(new BigDecimal(balance));

        return company;
    }


    private Map<String, Object> executeBehaviour() {
        bankFinancialStatus.tick();

        verify(eventPublisher).publish(captor.capture());

        return captor.getValue();
    }

    @Before
    public void setupBankEconomicStatus() {
        bankFinancialStatus = new BankFinancialStatus(world, bank);

        when(world.getEventPublisher()).thenReturn(eventPublisher);
    }

    @Before
    public void setupBank() {
        when(bank.getTreasuryAccount()).thenReturn(treasuryAccount);
        when(bank.getTreasuryAccount()).thenReturn(treasuryAccount);

        when(treasuryAccount.getBalance()).thenReturn(TREASURY_BALANCE);
    }

    private BankFinancialStatus bankFinancialStatus;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private Bank bank;

    @Mock
    private World world;

    @Captor
    private ArgumentCaptor<Map<String, Object>> captor;

    @Mock
    private Account treasuryAccount;

    private BigDecimal TREASURY_BALANCE = new BigDecimal("10000000");

    @Mock
    private Person person;

    @Mock
    private Person otherPerson;

    @Mock
    private Company company;

    @Mock
    private Company otherCompany;

    private static String IBAN = "iban";

    private static String OTHER_IBAN = "otherIban";
}
