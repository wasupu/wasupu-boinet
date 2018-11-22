package io.wasupu.boinet.financial.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.financial.Bank;
import io.wasupu.boinet.subjects.Behaviour;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.google.common.math.Quantiles.percentiles;

public class BankFinancialStatus implements Behaviour {

    public BankFinancialStatus(World world, Bank bank) {
        this.world = world;
        this.bank = bank;
    }

    @Override
    public void tick() {
        var treasuryAccountBalance = bank.getTreasuryAccount().getBalance();

        world.getEventPublisher().publish(Map.of(
            "eventType", "bankBalance",
            "treasuryAccount", treasuryAccountBalance,
            "peopleBalance", getPeopleBalance(),
            "companiesBalance", getCompaniesBalance(),
            "maxPeopleBalance", getMaxPeopleBalance(),
            "minPeopleBalance", getMinPeopleBalance(),
            "maxCompaniesBalance", getMaxCompaniesBalance(),
            "95thPercentileCompaniesBalance", get95thPercentileCompaniesBalance(),
            "95thPercentilePopulationBalance", get95thPercentilePopulationBalance(),
            "minCompaniesBalance", getMinCompaniesBalance()));
    }

    private BigDecimal get95thPercentileCompaniesBalance() {
        var companies = world.getCompanies();
        if (companies.isEmpty()) return BigDecimal.ZERO;

        var companyBalances = companies
            .stream()
            .map(company -> bank.getBalance(company.getIban()))
            .collect(Collectors.toList());

        return new BigDecimal(percentiles()
            .index(95)
            .compute(companyBalances));
    }

    private BigDecimal get95thPercentilePopulationBalance() {
        var population = world.getPopulation();
        if (population.isEmpty()) return BigDecimal.ZERO;

        var populationBalances = population
            .stream()
            .map(company -> bank.getBalance(company.getIban()))
            .collect(Collectors.toList());

        return new BigDecimal(percentiles()
            .index(95)
            .compute(populationBalances));
    }

    private BigDecimal getMaxPeopleBalance() {
        return world
            .getPopulation()
            .stream()
            .map(person -> bank.getBalance(person.getIban()))
            .max(BigDecimal::compareTo)
            .orElse(BigDecimal.ZERO);
    }

    private BigDecimal getMinPeopleBalance() {
        return world
            .getPopulation()
            .stream()
            .map(person -> bank.getBalance(person.getIban()))
            .min(BigDecimal::compareTo)
            .orElse(BigDecimal.ZERO);
    }

    private BigDecimal getPeopleBalance() {
        return world
            .getPopulation()
            .stream()
            .map(person -> bank.getBalance(person.getIban()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getCompaniesBalance() {
        return world
            .getCompanies()
            .stream()
            .map(company -> bank.getBalance(company.getIban()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getMaxCompaniesBalance() {
        return world
            .getCompanies()
            .stream()
            .map(company -> bank.getBalance(company.getIban()))
            .max(BigDecimal::compareTo)
            .orElse(BigDecimal.ZERO);
    }

    private BigDecimal getMinCompaniesBalance() {
        return world
            .getCompanies()
            .stream()
            .map(company -> bank.getBalance(company.getIban()))
            .min(BigDecimal::compareTo)
            .orElse(BigDecimal.ZERO);
    }


    @Override
    public String getIdentifier() {
        return identifier;
    }

    private World world;

    private String identifier = UUID.randomUUID().toString();

    private Bank bank;
}
