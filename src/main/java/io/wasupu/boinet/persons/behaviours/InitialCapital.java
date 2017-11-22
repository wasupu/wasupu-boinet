package io.wasupu.boinet.persons.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.persons.Person;

import java.math.BigDecimal;

public class InitialCapital {

    public InitialCapital(World world, Person person) {
        this.world = world;
        this.person = person;
    }

    public void tick() {
        if (person.getAge() != 0) return;

        world.getBank().deposit(person.getIban(), INITIAL_CAPITAL);
    }

    static final BigDecimal INITIAL_CAPITAL = new BigDecimal(1000);

    private World world;

    private Person person;
}
