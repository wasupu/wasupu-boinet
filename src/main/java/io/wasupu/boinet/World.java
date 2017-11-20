package io.wasupu.boinet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.IntStream;

import static net.logstash.logback.marker.Markers.appendEntries;

public class World {

    public static void main(String[] args) {
        World world = new World();
        world.init(120, 10);
        world.start();
    }

    public World() {
        GregorianCalendar calendar = new GregorianCalendar(2017, 9, 5);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        currentDate = calendar.getTime();
        System.out.println(currentDate);
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
            .peek(x -> wait(200))
            .forEach(tickNumber -> {
                logger.info(appendEntries(ImmutableMap.of("tick", tickNumber)), "Tick number");
                tickConsumers.forEach(Runnable::run);

                addDayToCurrentDate();
            });
    }

    private void addDayToCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        calendar.add(Calendar.DATE, 1);
        currentDate = calendar.getTime();
    }

    private void wait(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        Person newPerson = new Person(createPersonUniqueIdentifier(), this);
        population.add(newPerson);
        return newPerson;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public Collection<Person> getCandidates(BigDecimal initialCapital) {
        return employmentOffice.getCandidates(initialCapital);
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

    private Date currentDate;
}


