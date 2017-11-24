package io.wasupu.boinet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.wasupu.boinet.population.Hospital;
import io.wasupu.boinet.population.Person;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.IntStream;

import static net.logstash.logback.marker.Markers.appendEntries;

public class World {

    /**
     * @param args --population = Required. Number of people.
     *             --companies = Required. Number of companies.
     *             --number-of-ticks = Optional. If not specified, there will be infinite ticks
     *             --stream-service-api-key = Optional. Required if --stream-service-namespace is defined
     *             --stream-service-namespace = Optional. Required if --stream-service-api-key is defined
     */
    public static void main(String[] args) {
        Integer numberOfPeople = findArgument("--population", args)
            .map(Integer::new)
            .orElseThrow(() -> new IllegalArgumentException("--population argument required"));
        Integer numberOfCompanies = findArgument("--companies", args)
            .map(Integer::new)
            .orElseThrow(() -> new IllegalArgumentException("--companies argument required"));

        Optional<String> streamServiceApiKey = findArgument("--stream-service-api-key", args);
        Optional<String> streamServiceNamespace = findArgument("--stream-service-namespace", args);
        World world = (streamServiceApiKey.isPresent() && streamServiceNamespace.isPresent()) ? new World(streamServiceApiKey.get(), streamServiceNamespace.get()) : new World();

        world.init(numberOfPeople, numberOfCompanies);
        world.start(findArgument("--number-of-ticks", args).map(Integer::new));
    }

    private static Optional<String> findArgument(String argumentName, String[] args) {
        return Arrays.stream(args)
            .filter(argument -> argument.startsWith(argumentName))
            .map(argument -> argument.replaceFirst(argumentName + "=", ""))
            .filter(argumentValue -> !argumentValue.isEmpty())
            .findFirst();
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
            .forEach(hospital::newBorn);
    }

    public void start(Optional<Integer> numberOfTicks) {
        IntStream stream = numberOfTicks
            .map(integer -> IntStream.range(0, integer))
            .orElseGet(() -> IntStream.iterate(0, i -> i + 1));

        stream.forEach(tickNumber -> {
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

    public Person newSettler() {
        return hospital.newBorn(3);
    }

    public GPS getGPS() {
        return GPS;
    }

    private void newSupplier(Integer number) {
        companies.add(new Company(createCompanyUniqueIdentifier(), this));
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

    private EventPublisher eventPublisher;

    private final Hospital hospital = new Hospital(this);
    private GPS GPS = new GPS();
}


