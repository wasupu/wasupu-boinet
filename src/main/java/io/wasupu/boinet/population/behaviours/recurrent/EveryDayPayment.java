package io.wasupu.boinet.population.behaviours.recurrent;

import io.wasupu.boinet.ProductType;
import io.wasupu.boinet.World;
import io.wasupu.boinet.population.Person;

public class EveryDayPayment extends Payment {

    public EveryDayPayment(World world, Person person, ProductType productType, Integer startPriceRange, Integer endPriceRange) {
        super(world,
            person,
            productType, startPriceRange, endPriceRange);
        this.person = person;
    }

    public void tick() {
        if (person.getAge() < 2) return;

        super.tick();
    }

    private Person person;
}
