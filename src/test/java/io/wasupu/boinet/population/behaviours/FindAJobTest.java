package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.companies.Company;
import io.wasupu.boinet.population.Person;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FindAJobTest {

    @Test
    public void shouldFindAJobWhenIsUnemployed() {
        when(person.isUnemployed()).thenReturn(true);
        when(world.findBestCompanyToWork()).thenReturn(company);

        findAJob.tick();

        verify(company).hire(person);
    }

    @Test
    public void shouldNotFindAJobWhenIsEmployed() {
        when(person.isUnemployed()).thenReturn(false);
        findAJob.tick();

        verify(company, never()).hire(any());
    }

    @Before
    public void setupInitialCapital() {
        findAJob = new FindAJob(world, person);
    }

    private FindAJob findAJob;

    @Mock
    private World world;

    @Mock
    private Person person;

    @Mock
    private Company company;
}
