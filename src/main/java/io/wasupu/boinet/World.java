package io.wasupu.boinet;

import com.google.common.collect.ImmutableList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.IntStream;

public class World {

    public static void main(String[] args) {
        World world = new World();
        world.init(12, 5);
        world.start();
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
            .forEach(i -> {
                System.out.println("+++++++++++++++++++++++++++++++++++++++");
                System.out.println("TICK:" + i);

                tickConsumers.forEach(Runnable::run);

                System.out.println("+++++++++++++++++++++++++++++++++++++++");
            });

        System.out.println(companies.stream()
            .map(company -> {
                System.out.println(new BigDecimal(company.getEmployees().size()));
                return new BigDecimal(company.getEmployees().size());
            })
            .reduce(new BigDecimal(0), BigDecimal::add));
    }

    public Company findCompany() {
        return companies.stream().findFirst().get();
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

    private Collection<Company> companies = new ArrayList<>();

    private Collection<Person> population = new ArrayList<>();

    private Collection<Runnable> tickConsumers = ImmutableList.of();

    private Bank bank = new Bank();

    private EmploymentOffice employmentOffice = new EmploymentOffice(this);
}


