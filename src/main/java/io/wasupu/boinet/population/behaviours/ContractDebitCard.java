package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;
import io.wasupu.boinet.population.Person;

public class ContractDebitCard extends EconomicalSubjectBehaviour {

    public ContractDebitCard(World world, Person person) {
        super(world, person);
    }

    public void tick() {

        var person = (Person) getEconomicalSubject();
        if (person.getAge() != 0) return;

        person.setPan(getWorld().getBank().contractDebitCard(person.getIdentifier(), person.getIban()));
    }
}
