package io.wasupu.boinet.persons.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.persons.Person;

public class ContractAccountBehaviour {

    public ContractAccountBehaviour(World world, Person person) {
        this.world = world;
        this.person = person;
    }

    public void tick() {
        if (person.getAge() != 0) return;

        person.setIban(world.getBank().contractAccount());
    }

    private World world;

    private Person person;
}
