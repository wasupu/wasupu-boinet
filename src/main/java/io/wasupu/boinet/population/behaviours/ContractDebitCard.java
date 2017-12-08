package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.population.Person;

public class ContractDebitCard extends EconomicalSubjectBehaviour {

    public ContractDebitCard(World world, Person person) {
        super(world, person);
    }

    public void tick() {
        if (getEconomicalSubject().getAge() != 0) return;

        ((Person)getEconomicalSubject()).setPan(getWorld().getBank().contractDebitCard(getEconomicalSubject().getIban()));
    }
}
