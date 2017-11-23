package io.wasupu.boinet;

import com.github.javafaker.Faker;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.wasupu.boinet.persons.Person;
import io.wasupu.boinet.persons.behaviours.ContractAccount;
import io.wasupu.boinet.persons.behaviours.ContractDebitCard;
import io.wasupu.boinet.persons.behaviours.EveryDayRecurrentPayment;
import io.wasupu.boinet.persons.behaviours.GenerateRandomPrice;
import io.wasupu.boinet.persons.behaviours.InitialCapital;
import io.wasupu.boinet.persons.behaviours.MonthlyRecurrentPayment;
import io.wasupu.boinet.persons.behaviours.TriggeredByBalanceThreshold;
import io.wasupu.boinet.persons.behaviours.WeekendRecurrentPayment;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
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
        world.init(100, 5);
        world.start();
    }

    public World(String streamServiceApiKey, String streamServiceNamespace) {
        this();
        eventPublisher = new EventPublisher(streamServiceApiKey, streamServiceNamespace);
    }

    public World() {
        currentDate = new DateTime(2017, 10, 5, 0, 0, 0, DateTimeZone.UTC);
        eventPublisher = new EventPublisher();
    }

    public void init(Integer numberOfPeople, Integer numberOfCompanies) {
        IntStream.range(0, numberOfCompanies)
            .forEach(this::newSupplier);

        IntStream.range(0, numberOfPeople)
            .forEach(this::newSettler);
    }

    public void start(Integer... numberOfTicks) {
        int ticks = numberOfTicks.length == 0 ? 500 : numberOfTicks[0];

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

        newPerson.listenTicks(new ContractAccount(this, newPerson)::tick);
        newPerson.listenTicks(new ContractDebitCard(this, newPerson)::tick);
        newPerson.listenTicks(new InitialCapital(this, newPerson)::tick);

        withEating(newPerson);
        withPowerSupply(newPerson);
        withCountryside(newPerson);
        withCableTV(newPerson);
        withMortgage(newPerson);
        withInternetConnection(newPerson);

        population.add(newPerson);
        return newPerson;
    }

    private void withEating(Person newPerson) {
        newPerson.listenTicks(new EveryDayRecurrentPayment(this,
            newPerson,
            ProductType.MEAL,
            10,
            20)::tick);
    }

    private void withMortgage(Person newPerson) {
        newPerson.listenTicks(new MonthlyRecurrentPayment(this,
            newPerson,
            3,
            ProductType.MORTGAGE,
            generateRandomPrice.generateRandomPrice(300, 500),
            findCompany())::tick);
    }

    private void withPowerSupply(Person newPerson) {
        newPerson.listenTicks(new MonthlyRecurrentPayment(this,
            newPerson,
            25,
            ProductType.ELECTRICITY,
            60,
            120,
            findCompany())::tick);
    }

    private void withCountryside(Person newPerson) {
        newPerson.listenTicks(new TriggeredByBalanceThreshold(this,
            newPerson,
            new BigDecimal("1000"),
            new BigDecimal("6000"),
            new WeekendRecurrentPayment(this,
                newPerson,
                ProductType.ENTERTAINMENT,
                100,
                500))::tick);
    }

    private void withCableTV(Person newPerson) {
        newPerson.listenTicks(new TriggeredByBalanceThreshold(this,
            newPerson,
            new BigDecimal("1000"),
            new BigDecimal("2000"),
            new MonthlyRecurrentPayment(this,
                newPerson,
                5,
                ProductType.ENTERTAINMENT,
                generateRandomPrice.generateRandomPrice(10, 25),
                findCompany()))::tick);
    }

    private void withInternetConnection(Person newPerson) {
        newPerson.listenTicks(
            new TriggeredByBalanceThreshold(this,
                newPerson,
                new BigDecimal("1000"),
                new BigDecimal("1000"),
                new MonthlyRecurrentPayment(this,
                    newPerson,
                    5,
                    ProductType.INTERNET,
                    generateRandomPrice.generateRandomPrice(40, 100),
                    findCompany()))::tick);
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

    private GenerateRandomPrice generateRandomPrice = new GenerateRandomPrice();
}


