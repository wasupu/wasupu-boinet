package io.wasupu.boinet;

import com.google.common.collect.ImmutableList;
import io.wasupu.boinet.companies.BusinessIncubator;
import io.wasupu.boinet.companies.Company;
import io.wasupu.boinet.eventPublisher.EventPublisher;
import io.wasupu.boinet.financial.Bank;
import io.wasupu.boinet.population.Hospital;
import io.wasupu.boinet.population.Person;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static java.util.stream.IntStream.iterate;
import static java.util.stream.IntStream.range;
import static net.logstash.logback.marker.Markers.appendEntries;

public class World {

    public World(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        this.currentDate = new DateTime(2017, 10, 5, 0, 0, 0, DateTimeZone.UTC);
    }

    public void init(Integer numberOfPeople, Integer numberOfCompanies) {
        range(0, numberOfCompanies)
            .forEach(this::newSupplier);

        range(0, numberOfPeople)
            .forEach(hospital::newBorn);
    }

    public void start(Optional<Integer> numberOfTicks) {
        var stream = numberOfTicks
            .map(integer -> range(0, integer))
            .orElseGet(() -> iterate(0, i -> i + 1));

        stream.forEach(tickNumber -> {
            logger.info(appendEntries(Map.of("tick", tickNumber)), "Tick number");
            tickConsumers.forEach(Runnable::run);

            addDayToCurrentDate();
        });
    }

    private void addDayToCurrentDate() {
        currentDate = currentDate.plusDays(1);
    }

    public Company findBestCompanyToWork() {
        return businessIncubator.findBestCompanyToWork(companies, bank);
    }

    public Company findCompany() {
        return businessIncubator.findCompany(companies);
    }

    private void newSupplier(Integer number) {
        companies.add(businessIncubator.newCompany(this));
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

    public EventPublisher getEvenPublisher() {
        return eventPublisher;
    }

    public GPS getGPS() {
        return GPS;
    }

    private List<Company> companies = new ArrayList<>();

    private Collection<Person> population = new ArrayList<>();

    private Collection<Runnable> tickConsumers = List.of();

    private Bank bank = new Bank(this);

    private static Logger logger = LoggerFactory.getLogger(World.class);

    private DateTime currentDate;

    private EventPublisher eventPublisher;

    private final Hospital hospital = new Hospital(this);

    private GPS GPS = new GPS();

    private BusinessIncubator businessIncubator = new BusinessIncubator();


}


