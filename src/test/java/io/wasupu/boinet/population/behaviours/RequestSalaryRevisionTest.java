package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.companies.Company;
import io.wasupu.boinet.population.Person;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RequestSalaryRevisionTest {

    @Test
    public void it_should_request_salary_revision_if_not_have_enough_money() {
        when(person.getEmployer()).thenReturn(company);
        when(person.getAge()).thenReturn(2l);
        when(person.isUnemployed()).thenReturn(false);
        when(person.hasEnoughMoney()).thenReturn(false);

        requestSalaryIncrease.tick();

        verify(company).requestSalaryRevision(person);
    }

    @Test
    public void should_not_request_salary_revision_if_have_enough_money() {
        when(person.getAge()).thenReturn(2l);
        when(person.isUnemployed()).thenReturn(false);
        when(person.hasEnoughMoney()).thenReturn(true);

        requestSalaryIncrease.tick();

        verify(company, never()).requestSalaryRevision(person);
    }

    @Test
    public void it_should_not_request_salary_revision_if_it_unemployed() {
        when(person.getAge()).thenReturn(2l);
        when(person.isUnemployed()).thenReturn(true);

        requestSalaryIncrease.tick();

        verify(company, never()).requestSalaryRevision(person);
    }

    @Before
    public void requestSalaryIncrease() {
        requestSalaryIncrease = new RequestSalaryRevision(world, person);
    }

    private RequestSalaryRevision requestSalaryIncrease;

    @Mock
    private Company company;

    @Mock
    private Person person;

    @Mock
    private World world;
}
