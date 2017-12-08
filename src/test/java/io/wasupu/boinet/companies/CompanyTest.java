package io.wasupu.boinet.companies;

import com.google.common.testing.EqualsTester;
import io.wasupu.boinet.*;
import io.wasupu.boinet.companies.Company;
import io.wasupu.boinet.population.Person;
import org.apache.commons.lang3.tuple.Pair;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.IntStream;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

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
    public void shouldCanBuyAProductToTheCompany() {
        company.tick();
        company.buyProduct(PAN, ProductType.MEAL, PRICE);

        verify(bank).processPayment(PRICE,
            PAN,
            IBAN,
            COMPANY_IDENTIFIER,
            "meal",
            otherCoordinates);
    }

    @Test
    public void shouldPayTheEmployeesOnEvery28th() {
        when(person.getIban()).thenReturn(OTHER_IBAN);
        when(world.getCurrentDateTime()).thenReturn(new DateTime().withDayOfMonth(27));
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal(10000));

        company.setIban(IBAN);
        company.hire(person);
        company.tick();

        verify(bank).transfer(IBAN, OTHER_IBAN, company.getEmployeeSalary(person));
    }

    @Test
    public void shouldNotPayTheEmployeesOtherThan29th() {
        company.hire(person);

        IntStream.range(1, 27)
            .forEach(day -> {
                when(world.getCurrentDateTime()).thenReturn(new DateTime().withDayOfMonth(day));
                company.tick();
            });

        verify(bank, never()).transfer(eq(IBAN), eq(OTHER_IBAN), any());
    }

    @Test
    public void shouldPublishCompanyInfoAt0Ticks() {
        company.tick();

        verify(eventPublisher).publish( (Map<String, Object>) argThat(Matchers.<String, Object>hasEntry("company", "companyId")));
        verify(eventPublisher).publish( (Map<String, Object>) argThat(Matchers.<String, Object>hasEntry("balance", new BigDecimal("12"))));
        verify(eventPublisher).publish( (Map<String, Object>) argThat(Matchers.<String, Object>hasEntry("currency", "EUR")));
        verify(eventPublisher).publish( (Map<String, Object>) argThat(Matchers.<String, Object>hasEntry("date", CURRENT_DATE.toDate())));
    }

    @Test
    public void shouldPublishCompanyInfoWithAddress() {
        company.tick();

        verify(eventPublisher).publish((Map<String, Object>) argThat(hasKey("address")));
    }

    @Test
    public void shouldPublishCompanyInfoWithName() {
        company.tick();

        verify(eventPublisher).publish((Map<String, Object>) argThat(hasKey("name")));
    }

    @Test
    public void shouldPublishCompanyInfoAt30Ticks() {
        IntStream.range(0, 31).forEach(i -> company.tick());

        verify(eventPublisher, times(2)).publish( (Map<String, Object>) argThat(Matchers.<String, Object>hasEntry("company", "companyId")));
        verify(eventPublisher, times(2)).publish( (Map<String, Object>) argThat(Matchers.<String, Object>hasEntry("balance", new BigDecimal("12"))));
        verify(eventPublisher, times(2)).publish( (Map<String, Object>) argThat(Matchers.<String, Object>hasEntry("currency", "EUR")));
        verify(eventPublisher, times(2)).publish( (Map<String, Object>) argThat(Matchers.<String, Object>hasEntry("date", CURRENT_DATE.toDate())));
    }

    @Test
    public void shouldReviseTheSalaryOfAnEmployee() {
        company.tick();
        company.hire(person);
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal(16000));
        BigDecimal salary = company.getEmployeeSalary(person);

        company.requestSalaryRevision(person);

        assertNotEquals("The employee has change its salary", salary, company.getEmployeeSalary(person));
    }

    @Test
    public void shouldOnlyChangeTheSalaryCompanyBalanceIsPositive() {
        company.tick();
        company.hire(person);

        BigDecimal salary = company.getEmployeeSalary(person);
        company.requestSalaryRevision(person);

        assertEquals("The employee not has change its salary", salary, company.getEmployeeSalary(person));
    }

    @Test
    public void shouldPayBonusToEmployeesIfCompanyCan() {
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
    public void shouldNotPayBonusToEmployeesIfCompanyCan() {
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal(1000));

        company.tick();
        company.hire(person);
        company.tick();
        company.tick();
        company.tick();

        verify(bank, never()).transfer(any(), any(),any());
    }

    @Test
    public void shouldFireEmployeesThatCompanyCanPay() {
        when(person.getIban()).thenReturn(IBAN);
        when(world.getCurrentDateTime()).thenReturn(new DateTime().withDayOfMonth(27));
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal(1));
        company.setIban(IBAN);

        company.hire(person);
        company.tick();

        verify(person).youAreFired();
    }

    @Before
    public void setupGPS() {
        when(gps.coordinatesAround(coordinates.getLeft(), coordinates.getRight())).thenReturn(otherCoordinates);
        when(gps.coordinates()).thenReturn(coordinates);
        when(world.getGPS()).thenReturn(gps);
    }

    @Before
    public void setupEventPublisher() {
        when(world.getEventCompanyPublisher()).thenReturn(eventPublisher);
    }

    @Before
    public void setupCompanyAccount() {
        when(world.getBank()).thenReturn(bank);
        when(world.getCurrentDateTime()).thenReturn(new DateTime(CURRENT_DATE));
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

    private static final DateTime CURRENT_DATE = new DateTime(2017,1,1,0,0);

    @Mock
    private EventPublisher eventPublisher;

    private static final BigDecimal PRICE = new BigDecimal(10);

    @Mock
    private GPS gps;

    private Pair<Double, Double> coordinates = Pair.of(40.2, -3.7);
    private Pair<Double, Double> otherCoordinates = Pair.of(41.2, -3.9);
}