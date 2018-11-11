package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.companies.Company;
import io.wasupu.boinet.population.Person;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RequestSalaryRevisionYearlyTest {

    @Test
    public void it_should_request_salary_revision_every_year() {
        when(world.getCurrentDateTime()).thenReturn(new DateTime(2017,1,6,0,0));
        when(person.getEmployer()).thenReturn(company);
        when(person.getAge()).thenReturn(2l);
        when(person.isUnemployed()).thenReturn(false);

        requestSalaryIncrease.tick();

        verify(company).requestSalaryRevision(person);
    }

    @Test
    public void should_not_request_salary_revision_the_rest_of_days() {
        when(person.getAge()).thenReturn(2l);
        when(world.getCurrentDateTime()).thenReturn(new DateTime(2017,1,9,0,0));
        when(person.isUnemployed()).thenReturn(false);

        requestSalaryIncrease.tick();

        verify(company, never()).requestSalaryRevision(person);
    }

    @Test
    public void it_should_not_request_salary_revision_if_it_unemployed(){
        when(person.getAge()).thenReturn(2l);
        when(person.isUnemployed()).thenReturn(true);

        requestSalaryIncrease.tick();

        verify(company, never()).requestSalaryRevision(person);
    }

    @Before
    public void requestSalaryIncrease() {
        requestSalaryIncrease = new RequestSalaryRevisionYearly(world, person,6);
    }

    private RequestSalaryRevisionYearly requestSalaryIncrease;

    @Mock
    private Company company;

    @Mock
    private Person person;

    @Mock
    private World world;
}
