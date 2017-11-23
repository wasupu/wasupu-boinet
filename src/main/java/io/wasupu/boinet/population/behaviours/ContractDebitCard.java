package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.population.Person;

public class ContractDebitCard extends PersonBehaviour {

    public ContractDebitCard(World world, Person person) {
        super(world, person);
    }

    public void tick() {
        if (getPerson().getAge() != 0) return;

        getPerson().setPan(getWorld().getBank().contractDebitCard(getPerson().getIban()));
    }
}
