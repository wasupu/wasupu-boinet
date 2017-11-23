package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.population.Person;

public class ContractAccount extends PersonBehaviour {

    public ContractAccount(World world, Person person) {
        super(world, person);
    }

    public void tick() {
        if (getPerson().getAge() != 0) return;

        getPerson().setIban(getWorld().getBank().contractAccount());
    }
}
