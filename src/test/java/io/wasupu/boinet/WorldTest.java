package io.wasupu.boinet;

import io.wasupu.boinet.companies.BusinessIncubator;
import io.wasupu.boinet.companies.Company;
import io.wasupu.boinet.eventPublisher.EventPublisher;
import io.wasupu.boinet.financial.Bank;
import io.wasupu.boinet.population.Hospital;
import io.wasupu.boinet.population.Person;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest({World.class, Hospital.class, BusinessIncubator.class})
public class WorldTest {

    @Test
    public void shouldCreateBunchOfPeople() {
        world.init(NUMBER_OF_PEOPLE, NUMBER_OF_COMPANIES);

        assertThat(world.getPopulation()).isNotEmpty();
    }

    @Test
    public void shouldCreateGivenNumberOfPeople() {
        world.init(NUMBER_OF_PEOPLE, NUMBER_OF_COMPANIES);

        assertThat(world.getPopulation()).hasSize(NUMBER_OF_PEOPLE);
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

        assertThat(world.getCompanies()).isNotEmpty();
    }

    @Test
    public void shouldCreateGivenNumberOfCompanies() {
        world.init(NUMBER_OF_PEOPLE, NUMBER_OF_COMPANIES);

        assertThat(world.getCompanies()).hasSize(NUMBER_OF_COMPANIES);
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
        world.start(Optional.of(2));

        verify(tickConsumer, times(2)).run();
    }

    @Test
    public void shouldReturnADifferentCompanyInEveryCall() {
        world.init(NUMBER_OF_PEOPLE, NUMBER_OF_COMPANIES);

        Collection<Company> companies = new ArrayList<>();
        IntStream.range(0, 15).forEach(i -> companies.add(world.findCompany()));

        assertThat(companies)
            .contains(firstCompany, secondCompany);
    }

    @Test
    public void shouldTheHaveAnInitCurrentDateTime() {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(2017, 9, 5);
        gregorianCalendar.setTimeZone(TimeZone.getTimeZone("UTC"));

        assertNotNull("The current date must be not null", world.getCurrentDateTime());
        assertEquals("The date is not the expected", gregorianCalendar.getTime(), world.getCurrentDateTime().toDate());
    }

    @Test
    public void shouldReturnTheBestCompanyToWork() {
        when(firstCompany.getNumberOfEmployees()).thenReturn(0);
        when(firstCompany.getIban()).thenReturn(IBAN);

        when(secondCompany.getNumberOfEmployees()).thenReturn(0);
        when(secondCompany.getIban()).thenReturn(OTHER_IBAN);

        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal("3000"));
        when(bank.getBalance(OTHER_IBAN)).thenReturn(new BigDecimal("2000"));

        world.init(NUMBER_OF_PEOPLE, NUMBER_OF_COMPANIES);

        Company company = world.findBestCompanyToWork();

        assertNotNull("Company must be not null", company);
        assertEquals("The first company to work is company 1", firstCompany, company);
    }

    @Test
    public void shouldReturnTheBestCompanyToWorkSecondCompany() {
        when(firstCompany.getIdentifier()).thenReturn("first");
        when(firstCompany.getNumberOfEmployees()).thenReturn(6);
        when(firstCompany.getIban()).thenReturn(IBAN);

        when(secondCompany.getIdentifier()).thenReturn("second");
        when(secondCompany.getNumberOfEmployees()).thenReturn(0);
        when(secondCompany.getIban()).thenReturn(OTHER_IBAN);

        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal("3000"));
        when(bank.getBalance(OTHER_IBAN)).thenReturn(new BigDecimal("2000"));

        world.init(NUMBER_OF_PEOPLE, NUMBER_OF_COMPANIES);

        Company company = world.findBestCompanyToWork();

        assertNotNull("Company must be not null", company);
        assertEquals("The first company to work is company 2", secondCompany, company);
    }

    @Test
    public void shouldReturnTheBestCompanyToWorkFirstCompanyWhenEquals() {
        when(firstCompany.getNumberOfEmployees()).thenReturn(2);
        when(firstCompany.getIban()).thenReturn(IBAN);

        when(secondCompany.getNumberOfEmployees()).thenReturn(2);
        when(secondCompany.getIban()).thenReturn(OTHER_IBAN);

        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal("3000"));
        when(bank.getBalance(OTHER_IBAN)).thenReturn(new BigDecimal("2000"));

        world.init(NUMBER_OF_PEOPLE, NUMBER_OF_COMPANIES);

        Company company = world.findBestCompanyToWork();

        assertNotNull("Company must be not null", company);
        assertEquals("The first company to work is company 1", firstCompany, company);
    }

    @Before
    public void createWorld() throws Exception {
        whenNew(Company.class)
            .withAnyArguments()
            .thenReturn(firstCompany, secondCompany);

        whenNew(Person.class)
            .withAnyArguments()
            .thenReturn(firstPerson, secondPerson);

        whenNew(Bank.class).withAnyArguments().thenReturn(bank);
        world = new World(eventPublisher, SEED_CAPITAL);
    }
    @Mock(name = "firstPerson")
    private Person firstPerson;

    @Mock(name = "secondPerson")
    private Person secondPerson;

    @Mock(name = "firstCompany")
    private Company firstCompany;

    @Mock(name = "secondCompany")
    private Company secondCompany;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private Bank bank;

    private World world;

    private static final Integer NUMBER_OF_PEOPLE = 2;

    private static final Integer NUMBER_OF_COMPANIES = 2;

    private static final String IBAN = "";

    private static final String OTHER_IBAN = "";

    private static final BigDecimal SEED_CAPITAL = new BigDecimal(5_000_000);

}