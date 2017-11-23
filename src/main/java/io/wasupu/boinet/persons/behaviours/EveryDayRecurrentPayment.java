package io.wasupu.boinet.persons.behaviours;

import io.wasupu.boinet.ProductType;
import io.wasupu.boinet.World;
import io.wasupu.boinet.persons.Person;

public class EveryDayRecurrentPayment extends RecurrentPayment {

    public EveryDayRecurrentPayment(World world, Person person, ProductType productType, Integer startPriceRange, Integer endPriceRange) {
        super(world,
            person,
            productType, startPriceRange, endPriceRange);
        this.person = person;
    }

    public void tick() {
        if (person.getAge() < 2) return;

        executePayment();
    }

    private Person person;
}
