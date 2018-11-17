package io.wasupu.boinet.companies.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.companies.Company;
import io.wasupu.boinet.financial.Bank;
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
public class PayBonusTest {

    @Test
    public void it_should_pay_bonus_to_employees_if_company_can() {
        when(world.getBank()).thenReturn(bank);
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal("100001"));

        payBonus.tick();

        verify(company).payEmployee(person, new BigDecimal("40001.00"));
    }

    @Before
    public void setupBehaviour() {
        payBonus = new PayBonus(world, company);
    }

    @Before
    public void setupCompany() {
        when(company.getEmployees()).thenReturn(Map.of(person, PERSON_SALARY));
        when(company.getAge()).thenReturn(4l);
        when(company.getIban()).thenReturn(IBAN);
    }

    @Mock
    private Company company;

    @Mock
    private World world;

    @Mock
    private Bank bank;

    @Mock
    private Person person;

    private PayBonus payBonus;

    private static final String IBAN = "2";

    private static final BigDecimal PERSON_SALARY = new BigDecimal("100");

}
