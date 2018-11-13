package io.wasupu.boinet.companies;

import com.google.common.testing.EqualsTester;
import io.wasupu.boinet.GPS;
import io.wasupu.boinet.World;
import io.wasupu.boinet.financial.Bank;
import io.wasupu.boinet.population.Person;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static io.wasupu.boinet.economicalSubjects.EconomicalSubjectType.COMPANY;
import static java.util.stream.IntStream.range;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CompanyTest {

    @Test
    public void test_equals() {
        new EqualsTester()
            .addEqualityGroup(new Company("company1", world), new Company("company1", world))
            .addEqualityGroup(new Company("company2", world), new Company("company2", world))
            .testEquals();
    }


    @Test
    public void it_should_canbuy_a_product_to_the_company() {
        company.buyProduct(PAN, ProductType.MEAL, PRICE);

        verify(bank).payWithCard(PRICE,
            PAN,
            IBAN,
            COMPANY_IDENTIFIER,
            "meal",
            otherCoordinates);
    }

    @Test
    public void it_should_transfer_house_price_from_person_to_company() {
        var buyerIban = "IBAN-43";

        company.buyHouse(buyerIban, PRICE);

        verify(bank).transfer(buyerIban, IBAN, PRICE);
    }

    @Test
    public void it_should_pay_the_employees_on_every_28th() {
        when(person.getIban()).thenReturn(OTHER_IBAN);
        when(world.getCurrentDateTime()).thenReturn(new DateTime().withDayOfMonth(27));
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal(10000));


        company.hire(person);
        company.tick();

        verify(bank).transfer(IBAN, OTHER_IBAN, company.getEmployeeSalary(person));
    }

    @Test
    public void it_should_not_pay_the_employees_other_than_29th() {
        company.hire(person);

        range(1, 27)
            .forEach(day -> {
                when(world.getCurrentDateTime()).thenReturn(new DateTime().withDayOfMonth(day));
                company.tick();
            });

        verify(bank, never()).transfer(eq(IBAN), eq(OTHER_IBAN), any());
    }


    @Test
    public void it_should_revise_the_salary_of_an_employee() {
        company.tick();
        company.hire(person);
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal(16000));
        var salary = company.getEmployeeSalary(person);

        company.requestSalaryRevision(person);

        assertNotEquals("The employee has change its salary", salary, company.getEmployeeSalary(person));
    }

    @Test
    public void it_should_only_change_the_salary_company_balance_is_positive() {
        company.tick();
        company.hire(person);

        var salary = company.getEmployeeSalary(person);
        company.requestSalaryRevision(person);

        assertEquals("The employee not has change its salary", salary, company.getEmployeeSalary(person));
    }

    @Test
    @Ignore
    public void it_should_pay_bonus_to_employees_if_company_can() {
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal(100000));
        when(person.getIban()).thenReturn(OTHER_IBAN);

        company.tick();
        company.hire(person);
        company.tick();
        company.tick();
        company.tick();

        verify(bank).transfer(IBAN, OTHER_IBAN, new BigDecimal("40000.00"));
    }

    @Test
    public void it_should_not_pay_bonus_to_employees_if_company_can() {
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal(1000));

        company.tick();
        company.hire(person);
        company.tick();
        company.tick();
        company.tick();

        verify(bank, never()).transfer(any(), any(), any());
    }

    @Test
    public void it_should_fire_employees_that_company_can_pay() {
        when(person.getIban()).thenReturn(IBAN);
        when(world.getCurrentDateTime()).thenReturn(new DateTime().withDayOfMonth(27));
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal(1));
        company.setIban(IBAN);

        company.hire(person);
        company.tick();

        verify(person).youAreFired();
    }

    @Test
    public void it_should_return_company_subject_type() {
        assertEquals("Returned subject type must be COMPANY", COMPANY, company.getType());
    }

    @Before
    public void setupGPS() {
        when(gps.coordinatesAround(coordinates.getLeft(), coordinates.getRight())).thenReturn(otherCoordinates);
        when(gps.coordinates()).thenReturn(coordinates);
        when(world.getGPS()).thenReturn(gps);
    }

    @Before
    public void setupCompanyAccount() {
        when(world.getBank()).thenReturn(bank);
        when(world.getCurrentDateTime()).thenReturn(new DateTime(CURRENT_DATE));
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal(12));
    }

    @Before
    public void setupCompany() {
        company = new Company(COMPANY_IDENTIFIER, world);
        company.setIban(IBAN);
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

    private static final DateTime CURRENT_DATE = new DateTime(2017, 1, 1, 0, 0);


    private static final BigDecimal PRICE = new BigDecimal(10);

    @Mock
    private GPS gps;

    private Pair<Double, Double> coordinates = Pair.of(40.2, -3.7);
    private Pair<Double, Double> otherCoordinates = Pair.of(41.2, -3.9);
}