package io.wasupu.boinet;

import com.google.common.collect.ImmutableList;
import com.google.common.testing.EqualsTester;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static junit.framework.TestCase.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

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
        company.buyProduct(PAN);

        verify(bank).processPayment(new BigDecimal(10), PAN, IBAN, COMPANY_IDENTIFIER);
    }

    @Test
    public void shouldPayTheEmployeesEvery30Ticks() {
        when(person.getIban()).thenReturn(OTHER_IBAN);

        company.hire(person);
        IntStream.range(0, 31).forEach(i -> company.tick());

        verify(bank).transfer(IBAN, OTHER_IBAN, Company.SALARY);
    }

    @Test
    public void shouldPayTheEmployeesTwiceAt60Ticks() {
        when(person.getIban()).thenReturn(OTHER_IBAN);

        company.hire(person);
        IntStream.range(0, 61).forEach(i -> company.tick());

        verify(bank, times(2)).transfer(IBAN, OTHER_IBAN, Company.SALARY);
    }

    @Test
    public void shouldPublishCompanyInfoAt0Ticks() {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        company.tick();

        assertEquals("The balance string is not the expected",
            BALANCE_JSON + "\n", out.toString());
    }

    @Test
    public void shouldPublishCompanyInfoAt90Ticks() {

        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        IntStream.range(0, 91).forEach(i -> company.tick());

        assertEquals("The balance string is not the expected",
            BALANCE_JSON + "\n" + BALANCE_JSON + "\n", out.toString());
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

    private static final String CURRENT_DATE = "2017-10-05T14:48:00.000Z";

    private static final String BALANCE_JSON =
        "{" +
            "\"company\":\"companyId\"," +
            "\"balance\":12," +
            "\"currency\":\"EUR\"," +
            "\"date\":\""+ CURRENT_DATE + "\"" +
            "}";
}