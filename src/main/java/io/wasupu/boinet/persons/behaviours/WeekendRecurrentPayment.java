package io.wasupu.boinet.persons.behaviours;

import io.wasupu.boinet.ProductType;
import io.wasupu.boinet.World;
import io.wasupu.boinet.persons.Person;

public class WeekendRecurrentPayment extends RecurrentPayment {

    public WeekendRecurrentPayment(World world,
                                   Person person,
                                   ProductType productType,
                                   Integer startPriceRange,
                                   Integer endPriceRange) {
        super(world, person, productType, startPriceRange, endPriceRange);
        this.world = world;
    }

    public void tick() {
        if (!isWeekend()) return;

        executePayment();
    }

    private boolean isWeekend() {
        return world.getCurrentDateTime().getDayOfWeek() > 5;
    }

    private World world;
}
