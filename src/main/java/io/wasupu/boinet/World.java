package io.wasupu.boinet;

import com.google.common.collect.ImmutableList;
import io.wasupu.boinet.companies.BusinessIncubator;
import io.wasupu.boinet.companies.Company;
import io.wasupu.boinet.eventPublisher.EventPublisher;
import io.wasupu.boinet.financial.Bank;
import io.wasupu.boinet.financial.behaviours.BankEconomicStatus;
import io.wasupu.boinet.financial.behaviours.CalculateDifferenceBetweenIncomeAndExpenses;
import io.wasupu.boinet.population.Hospital;
import io.wasupu.boinet.population.Person;
import io.wasupu.boinet.subjects.Behaviour;
import io.wasupu.boinet.subjects.behaviours.Monthly;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

import static java.util.stream.IntStream.iterate;
import static java.util.stream.IntStream.range;
import static net.logstash.logback.marker.Markers.appendEntries;

public class World {

    public World(EventPublisher eventPublisher, BigDecimal seedCapital) {
        this.eventPublisher = eventPublisher;
        this.currentDate = new DateTime(2017, 10, 5, 0, 0, 0, DateTimeZone.UTC);
        this.bank = createBank(seedCapital);
    }

    private Bank createBank(BigDecimal seedCapital) {
        var bank = new Bank(this, seedCapital);

        bank.addBehaviour(withEconomicStatus(bank));
        bank.addBehaviour(withCalculateDifferenceBetweenAccounts(bank));

        listenTicks(bank::tick);

        return bank;
    }

    private Behaviour withEconomicStatus(Bank bank) {
        return new Monthly(this,
            27,
            new BankEconomicStatus(this, bank));

    }

    private Behaviour withCalculateDifferenceBetweenAccounts(Bank bank){
        return new Monthly(this,
            1,
            new CalculateDifferenceBetweenIncomeAndExpenses(bank));
    }

    public void init(Integer numberOfPeople, Integer numberOfCompanies) {
        range(0, numberOfCompanies).forEach(this::newSupplier);
        range(0, numberOfPeople).forEach(this::newBorn);
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

    private void newBorn(Integer number) {
        population.add(hospital.newBorn());
    }

    public void listenTicks(Runnable tickConsumer) {
        tickConsumers = ImmutableList
            .<Runnable>builder()
            .addAll(tickConsumers)
            .add(tickConsumer)
            .build();
    }

    public Bank getBank() {
        return bank;
    }

    public DateTime getCurrentDateTime() {
        return currentDate;
    }

    public EventPublisher getEventPublisher() {
        return eventPublisher;
    }

    public GPS getGPS() {
        return GPS;
    }

    public Collection<Person> getPopulation() {
        return population;
    }

    public Collection<Company> getCompanies() {
        return companies;
    }

    private List<Company> companies = new ArrayList<>();

    private Collection<Person> population = new ArrayList<>();

    private Collection<Runnable> tickConsumers = List.of();

    private Bank bank;

    private static Logger logger = LoggerFactory.getLogger(World.class);

    private DateTime currentDate;

    private EventPublisher eventPublisher;

    private final Hospital hospital = new Hospital(this);

    private GPS GPS = new GPS();

    private BusinessIncubator businessIncubator = new BusinessIncubator();


}


