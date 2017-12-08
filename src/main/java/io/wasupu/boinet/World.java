package io.wasupu.boinet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.wasupu.boinet.companies.Company;
import io.wasupu.boinet.financial.Bank;
import io.wasupu.boinet.population.Hospital;
import io.wasupu.boinet.population.Person;
import io.wasupu.boinet.economicalSubjects.behaviours.ContractAccount;
import io.wasupu.boinet.economicalSubjects.behaviours.InitialCapital;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
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

        personEventPublisher = new EventPublisher(PERSON_STREAM_ID,
            streamServiceApiKey,
            streamServiceNamespace);

        cardEventPublisher = new EventPublisher(CARD_STREAM_ID,
            streamServiceApiKey,
            streamServiceNamespace);

        companyEventPublisher = new EventPublisher(COMPANY_STREAM_ID,
            streamServiceApiKey,
            streamServiceNamespace);
    }

    public World() {
        currentDate = new DateTime(2017, 10, 5, 0, 0, 0, DateTimeZone.UTC);

        personEventPublisher = new EventPublisher(PERSON_STREAM_ID);
        cardEventPublisher = new EventPublisher(CARD_STREAM_ID);
        companyEventPublisher = new EventPublisher(COMPANY_STREAM_ID);
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

    public DateTime getCurrentDateTime() {
        return currentDate;
    }

    public EventPublisher getEventCardEventPublisher() {
        return cardEventPublisher;
    }

    public EventPublisher getEventPersonPublisher() {
        return personEventPublisher;
    }

    public EventPublisher getEventCompanyPublisher() {
        return companyEventPublisher;
    }

    public GPS getGPS() {
        return GPS;
    }

    public Company findBestCompanyToWork() {
        return companies
            .stream()
            .sorted(Comparator
                .comparingDouble(this::getCompanyRatio)
                .reversed())
            .collect(toList())
            .get(0);
    }

    private double getCompanyRatio(Company company) {
        return bank.getBalance(company.getIban()).doubleValue() / (company.getNumberOfEmployees() + 1);
    }

    private void newSupplier(Integer number) {
        Company company = new Company(createCompanyUniqueIdentifier(), this);

        company.listenTicks(new ContractAccount(this, company)::tick);
        company.listenTicks(new InitialCapital(this, company, new BigDecimal(60000))::tick);

        companies.add(company);
    }

    private String createCompanyUniqueIdentifier() {
        return UUID.randomUUID().toString();
    }

    private List<Company> companies = new ArrayList<>();

    private Collection<Person> population = new ArrayList<>();

    private Collection<Runnable> tickConsumers = ImmutableList.of();

    private Bank bank = new Bank(this);
    private static Logger logger = LoggerFactory.getLogger(World.class);

    private DateTime currentDate;

    private EventPublisher personEventPublisher;
    private EventPublisher cardEventPublisher;
    private EventPublisher companyEventPublisher;


    private final Hospital hospital = new Hospital(this);

    private GPS GPS = new GPS();

    private static final String PERSON_STREAM_ID = "personEventStream";
    private static final String COMPANY_STREAM_ID = "companyEventStream";
    private static final String CARD_STREAM_ID = "cardMovementEventStream";

}


