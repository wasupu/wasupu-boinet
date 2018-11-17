package io.wasupu.boinet.financial.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.financial.Bank;
import io.wasupu.boinet.subjects.Behaviour;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public class BankEconomicStatus implements Behaviour {

    public BankEconomicStatus(World world, Bank bank) {
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
            "companiesBalance", getCompaniesBalance()));
    }

    private BigDecimal getPeopleBalance() {
        return world
            .getPopulation()
            .stream()
            .map(person -> bank.getBalance(person.getIban()))
            .reduce(new BigDecimal(0), BigDecimal::add);
    }

    private BigDecimal getCompaniesBalance() {
        return world
            .getCompanies()
            .stream()
            .map(company -> bank.getBalance(company.getIban()))
            .reduce(new BigDecimal(0), BigDecimal::add);
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    private World world;

    private String identifier = UUID.randomUUID().toString();

    private Bank bank;
}
