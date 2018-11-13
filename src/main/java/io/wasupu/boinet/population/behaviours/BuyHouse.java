package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.companies.Company;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;
import io.wasupu.boinet.economicalSubjects.behaviours.balance.WhenBalanceExceedsThreshold;
import io.wasupu.boinet.economicalSubjects.behaviours.recurrent.Monthly;
import io.wasupu.boinet.financial.MortgageRejected;
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

        try {
            contractAMortgage(person);
            company.buyHouse(person.getIban(), amount);
        } catch (MortgageRejected mortgageRejected) {
        }
    }

    private void contractAMortgage(Person person) throws MortgageRejected {
        var bank = getWorld().getBank();
        var mortgageId = bank.contractMortgage(person.getIdentifier(), person.getIban(), amount);

        person.addBehaviour(new WhenBalanceExceedsThreshold(getWorld(), person,
            new BigDecimal("50"),
            new Monthly(getWorld(),
                person,
                28,
                new PayMortgage(getWorld(),
                    person,
                    generateRandomPrice.apply(500, 800)))));
        person.setMortgageIdentifier(mortgageId);
    }

    private BigDecimal amount = new BigDecimal(240000);

    private final Company company;

    private GenerateRandomPrice generateRandomPrice = new GenerateRandomPrice();
}