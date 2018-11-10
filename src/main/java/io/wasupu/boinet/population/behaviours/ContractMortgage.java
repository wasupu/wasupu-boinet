package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;
import io.wasupu.boinet.population.Person;

import java.math.BigDecimal;

public class ContractMortgage extends EconomicalSubjectBehaviour {

    public ContractMortgage(World world, Person person) {
        super(world, person);
    }

    public void tick() {
        var person = (Person) getEconomicalSubject();
        if (person.getAge() != 0) return;

        person.setMortgageIdentifier(getWorld().getBank().contractMortgage(person.getIdentifier(), person.getIban(), amount));
    }

    private BigDecimal amount = new BigDecimal(300000);
}
