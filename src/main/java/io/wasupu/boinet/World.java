package io.wasupu.boinet;

import com.github.javafaker.Faker;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

import static net.logstash.logback.marker.Markers.appendEntries;

public class World {

    /**
     * @param args arg0 = streamServiceApiKey
     *             arg1 = streamServiceNamespace
     */
    public static void main(String[] args) {
        World world = (args.length == 2) ? new World(args[0], args[1]) : new World();
        world.init(10, 2);
        world.start();
    }

    public World(String streamServiceApiKey, String streamServiceNamespace) {
        this();
        eventPublisher = new EventPublisher(streamServiceApiKey, streamServiceNamespace);
    }

    public World() {
        currentDate = new DateTime(2017, 10, 5,0,0,0, DateTimeZone.UTC);
        eventPublisher = new EventPublisher();
    }

    public void init(Integer numberOfPeople, Integer numberOfCompanies) {
        IntStream.range(0, numberOfPeople)
            .forEach(this::newSettler);

        IntStream.range(0, numberOfCompanies)
            .forEach(this::newSupplier);
    }

    public void start(Integer... numberOfTicks) {
        int ticks = numberOfTicks.length == 0 ? 1000 : numberOfTicks[0];

        IntStream.range(0, ticks)
            .forEach(tickNumber -> {
                logger.info(appendEntries(ImmutableMap.of("tick", tickNumber)), "Tick number");
                tickConsumers.forEach(Runnable::run);

                addDayToCurrentDate();
            });
    }

    private void addDayToCurrentDate() {
        currentDate = currentDate.plusDays(1);
    }

    public Company findCompany() {
        Random random = new Random();

        int randomNumber = random.nextInt(companies.size());
        return companies.get(randomNumber);
    }

    public void listenTicks(Runnable tickConsumer) {
        tickConsumers = ImmutableList
            .<Runnable>builder()
            .addAll(tickConsumers)
            .add(tickConsumer)
            .build();
    }

    public Collection<Person> getPopulation() {
        return population;
    }

    public Collection<Company> getCompanies() {
        return companies;
    }

    public Bank getBank() {
        return bank;
    }

    Person newSettler(Integer number) {
        Person newPerson = new Person(
            createPersonUniqueIdentifier(),
            faker.name().fullName(),
            faker.phoneNumber().cellPhone(),
            this);

        population.add(newPerson);
        return newPerson;
    }

    @Deprecated
    public Date getCurrentDate() {
        return currentDate.toDate();
    }

    public DateTime getCurrentDateTime() {
        return currentDate;
    }


    public Collection<Person> getCandidates(BigDecimal initialCapital) {
        return employmentOffice.getCandidates(initialCapital);
    }

    public EventPublisher getEventPublisher() {
        return eventPublisher;
    }

    private void newSupplier(Integer number) {
        companies.add(new Company(createCompanyUniqueIdentifier(), this));
    }

    private String createPersonUniqueIdentifier() {
        return UUID.randomUUID().toString();
    }

    private String createCompanyUniqueIdentifier() {
        return UUID.randomUUID().toString();
    }

    private List<Company> companies = new ArrayList<>();

    private Collection<Person> population = new ArrayList<>();

    private Collection<Runnable> tickConsumers = ImmutableList.of();
    private Bank bank = new Bank(this);

    private EmploymentOffice employmentOffice = new EmploymentOffice(this);

    private static Logger logger = LoggerFactory.getLogger(World.class);

    private DateTime currentDate;

    private Faker faker = new Faker();

    private EventPublisher eventPublisher;
}


