package io.wasupu.boinet.persons.behaviours;

import io.wasupu.boinet.ProductType;
import io.wasupu.boinet.World;
import io.wasupu.boinet.persons.Person;

public class EveryDayRecurrentPayment extends RecurrentPayment {

    public EveryDayRecurrentPayment(World world, Person person) {
        super(world,
            person,
            ProductType.MEAL,10,20);
        this.person = person;
    }

    public void tick() {
        if (person.getAge() < 2) return;

        executePayment();
    }

    private Person person;
}
