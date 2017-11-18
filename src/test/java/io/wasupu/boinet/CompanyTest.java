package io.wasupu.boinet;

import com.google.common.collect.ImmutableList;
import com.google.common.testing.EqualsTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

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

    @Before
    public void setupCompanyAccount() {
        when(world.getBank()).thenReturn(bank);
        when(bank.contractAccount()).thenReturn(IBAN);
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

    private static String COMPANY_IDENTIFIER = "identifier";

    private static final String IBAN = "2";

    private static final String OTHER_IBAN = "6";

    private static final String PAN = "12312312312";

}