package io.wasupu.boinet.persons.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.persons.Person;

import java.math.BigDecimal;

public class InitialCapital extends PersonBehaviour {

    public InitialCapital(World world, Person person) {
        super(world, person);
    }

    public void tick() {
        if (getPerson().getAge() != 0) return;

        getWorld().getBank().deposit(getPerson().getIban(), INITIAL_CAPITAL);
    }

    static final BigDecimal INITIAL_CAPITAL = new BigDecimal(1000);
}
