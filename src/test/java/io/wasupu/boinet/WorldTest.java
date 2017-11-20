package io.wasupu.boinet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(World.class)
public class WorldTest {

    @Test
    public void shouldCreateBunchOfPeople() {
        world.init(NUMBER_OF_PEOPLE, NUMBER_OF_COMPANIES);

        assertThat(world.getPopulation())
            .isNotEmpty();
    }

    @Test
    public void shouldCreateGivenNumberOfPeople() {
        world.init(NUMBER_OF_PEOPLE, NUMBER_OF_COMPANIES);

        assertThat(world.getPopulation())
            .hasSize(NUMBER_OF_PEOPLE);
    }

    @Test
    public void shouldCreateDifferentPeople() {
        world.init(NUMBER_OF_PEOPLE, NUMBER_OF_COMPANIES);

        Iterator<Person> populationIterator = world.getPopulation().iterator();
        assertNotEquals("The first two people must be different", populationIterator.next(), populationIterator.next());
    }

    @Test
    public void shouldCreateBunchOfCompanies() {
        world.init(NUMBER_OF_PEOPLE, NUMBER_OF_COMPANIES);

        assertThat(world.getCompanies())
            .isNotEmpty();
    }

    @Test
    public void shouldCreateGivenNumberOfCompanies() {
        world.init(NUMBER_OF_PEOPLE, NUMBER_OF_COMPANIES);

        assertThat(world.getCompanies())
            .hasSize(NUMBER_OF_COMPANIES);
    }

    @Test
    public void shouldCreateDifferentCompanies() {
        world.init(NUMBER_OF_PEOPLE, NUMBER_OF_COMPANIES);

        Iterator<Company> companiesIterator = world.getCompanies().iterator();
        assertNotEquals("The first two companies must be different", companiesIterator.next(), companiesIterator.next());
    }

    @Test
    public void shouldCreateTheWorldBank() {
        assertNotNull("Returned bank must not be null", world.getBank());
    }

    @Test
    public void shouldRunEveryTickConsumerOnStart() {
        Runnable tickConsumer = mock(Runnable.class);

        world.init(NUMBER_OF_PEOPLE, NUMBER_OF_COMPANIES);
        world.listenTicks(tickConsumer);
        world.start(2);

        verify(tickConsumer, times(2)).run();
    }

    @Test
    public void shouldReturnADifferentCompanyInEveryCall() {
        world.init(NUMBER_OF_PEOPLE, NUMBER_OF_COMPANIES);

        Collection<Company> companies = new ArrayList<>();
        IntStream.range(0,15).forEach(i -> companies.add(world.findCompany()));

        assertThat(companies)
            .contains(firstCompany,secondCompany);
    }

    @Before
    public void setupPeople() throws Exception {
        whenNew(Person.class)
            .withAnyArguments()
            .thenReturn(firstPerson, secondPerson);
    }

    @Before
    public void setupCompany() throws Exception {
        whenNew(Company.class)
            .withAnyArguments()
            .thenReturn(firstCompany, secondCompany);
    }

    @Mock(name = "firstPerson")
    private Person firstPerson;

    @Mock(name = "secondPerson")
    private Person secondPerson;

    @Mock(name = "firstCompany")
    private Company firstCompany;

    @Mock(name = "secondCompany")
    private Company secondCompany;

    private World world = new World();

    private static final Integer NUMBER_OF_PEOPLE = 2;

    private static final Integer NUMBER_OF_COMPANIES = 2;

}