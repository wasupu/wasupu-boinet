package io.wasupu.boinet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.testing.EqualsTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.IntStream;

import static junit.framework.TestCase.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CompanyTest {

    @Test
    public void testEquals() {
        new EqualsTester()
            .addEqualityGroup(new Company("company1", world), new Company("company1", world))
            .addEqualityGroup(new Company("company2", world), new Company("company2", world))
            .testEquals();
    }

    @Test
    public void shouldContractAnAccountInFirstTick() {
        company.tick();

        assertNotNull("After first tick must have an account", company.getIban());
        assertEquals("After first tick must have the expected iban", IBAN, company.getIban());
    }

    @Test
    public void shouldNotContractAgainAnAccountIfHasOneInOtherTick() {
        when(world.getBank()).thenReturn(bank);
        when(bank.contractAccount())
            .thenReturn(IBAN)
            .thenReturn(OTHER_IBAN);

        company.tick();
        company.tick();

        assertNotNull("After second tick must have an account", company.getIban());
        assertEquals("After second tick must the same account that have in first tick",
            IBAN, company.getIban());
    }

    @Test
    public void shouldDoAnInitialDepositOfCapital() {
        company.tick();

        verify(bank).deposit(IBAN, Company.INITIAL_CAPITAL);
    }

    @Test
    public void shouldNotRepeatAnInitialDepositOfCapital() {
        company.tick();
        company.tick();

        verify(bank, times(1)).deposit(IBAN, Company.INITIAL_CAPITAL);
    }

    @Test
    public void shouldHireEmployeesOnFirstTick() {
        ImmutableList<Person> candidates = ImmutableList.of(person);
        when(world.getCandidates(Company.INITIAL_CAPITAL)).thenReturn(candidates);

        company.tick();
        company.tick();

        assertThat(company.getEmployees())
            .as("The company must hire workers in first tick")
            .isNotEmpty()
            .containsExactly(person);
    }

    @Test
    public void shouldNotHireEmployeesTwice() {
        ImmutableList<Person> candidates = ImmutableList.of(person);
        when(world.getCandidates(Company.INITIAL_CAPITAL)).thenReturn(candidates);

        company.tick();
        company.tick();

        assertThat(company.getEmployees())
            .as("The company must hire workers in first tick")
            .isNotEmpty()
            .containsExactly(person);
    }

    @Test
    public void shouldChangeEmployeesStateToHired() {
        ImmutableList<Person> candidates = ImmutableList.of(person);
        when(world.getCandidates(Company.INITIAL_CAPITAL)).thenReturn(candidates);

        company.tick();
        company.tick();

        verify(person).youAreHired();
    }

    @Test
    public void shouldCanBuyAProductToTheCompany() {
        company.tick();
        company.buyProduct(PAN, PRICE);

        verify(bank).processPayment(PRICE, PAN, IBAN, COMPANY_IDENTIFIER);
    }

    @Test
    public void shouldPayTheEmployeesEvery29Ticks() {
        when(person.getIban()).thenReturn(OTHER_IBAN);

        company.hire(person);
        IntStream.range(0, 30).forEach(i -> company.tick());

        verify(bank).transfer(IBAN, OTHER_IBAN, Company.SALARY);
    }

    @Test
    public void shouldPayTheEmployeesTwiceAt60Ticks() {
        when(person.getIban()).thenReturn(OTHER_IBAN);

        company.hire(person);
        IntStream.range(0, 59).forEach(i -> company.tick());

        verify(bank, times(2)).transfer(IBAN, OTHER_IBAN, Company.SALARY);
    }

    @Test
    public void shouldPublishCompanyInfoAt0Ticks() {
        company.tick();

        verify(eventPublisher).publish("companyEventStream", COMPANY_INFO);
    }

    @Test
    public void shouldPublishCompanyInfoAt90Ticks() {
        IntStream.range(0, 91).forEach(i -> company.tick());

        verify(eventPublisher, times(2)).publish("companyEventStream", COMPANY_INFO);
    }

    @Before
    public void setupEventPublisher() {
        when(world.getEventPublisher()).thenReturn(eventPublisher);
    }

    @Before
    public void setupCompanyAccount() {
        when(world.getBank()).thenReturn(bank);

        when(world.getCurrentDate()).thenReturn(CURRENT_DATE);
        when(bank.contractAccount()).thenReturn(IBAN);
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal(12));
    }

    @Before
    public void setupCompany() {
        company = new Company(COMPANY_IDENTIFIER, world);
    }


    private Company company;

    @Mock
    private Person person;

    @Mock
    private Bank bank;

    @Mock
    private World world;

    private static String COMPANY_IDENTIFIER = "companyId";

    private static final String IBAN = "2";

    private static final String OTHER_IBAN = "6";

    private static final String PAN = "12312312312";

    private static final Date CURRENT_DATE = new Date();

    private static final Map<String, Object> COMPANY_INFO = ImmutableMap
        .<String, Object>builder()
        .put("company", "companyId")
        .put("balance", new BigDecimal("12"))
        .put("currency", "EUR")
        .put("date", CURRENT_DATE)
        .build();

    @Mock
    private EventPublisher eventPublisher;

    private static final BigDecimal PRICE = new BigDecimal(10);
}