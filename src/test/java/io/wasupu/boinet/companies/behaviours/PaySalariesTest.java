package io.wasupu.boinet.companies.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.companies.Company;
import io.wasupu.boinet.population.Person;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PaySalariesTest {

    @Test
    public void it_should_pay_bonus_to_employees_if_company_can() {
        paySalaries.tick();

        verify(company).payEmployee(person, new BigDecimal("100"));
    }

    @Before
    public void setupBehaviour() {
        paySalaries = new PaySalaries(world, company);
    }

    @Before
    public void setupCompany() {
        when(company.getEmployees()).thenReturn(Map.of(person, PERSON_SALARY));
    }

    @Mock
    private Company company;

    @Mock
    private World world;

    @Mock
    private Person person;

    private PaySalaries paySalaries;

    private static final BigDecimal PERSON_SALARY = new BigDecimal("100");

}
