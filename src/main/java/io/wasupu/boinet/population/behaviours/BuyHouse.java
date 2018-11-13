package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.companies.Company;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;
import io.wasupu.boinet.population.Person;

import java.math.BigDecimal;

public class BuyHouse extends EconomicalSubjectBehaviour {

    public BuyHouse(World world, Person person, Company company) {
        super(world, person);
        this.company = company;
    }

    public void tick() {
        var person = (Person) getEconomicalSubject();
        if (person.getAge() != 0) return;

        var bank = getWorld().getBank();
        var mortgageId = bank.contractMortgage(person.getIdentifier(), person.getIban(), amount);
        person.setMortgageIdentifier(mortgageId);
        company.buyHouse(person.getIban(), amount);
    }

    private BigDecimal amount = new BigDecimal(240000);

    private final Company company;
}
