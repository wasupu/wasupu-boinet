package io.wasupu.boinet.population.behaviours.recurrent;

import io.wasupu.boinet.ProductType;
import io.wasupu.boinet.World;
import io.wasupu.boinet.population.Person;
import io.wasupu.boinet.population.behaviours.recurrent.RecurrentPayment;

public class WeeklyRecurrentPayment extends RecurrentPayment {

    public WeeklyRecurrentPayment(World world,
                                  Person person,
                                  ProductType productType,
                                  Integer startPriceRange,
                                  Integer endPriceRange, Integer day) {
        super(world, person, productType, startPriceRange, endPriceRange);
        this.day = day;
    }

    public void tick() {
        if (!isWeekend()) return;

        executePayment();
    }

    private boolean isWeekend() {
        return getWorld().getCurrentDateTime().getDayOfWeek() == day;
    }

    private int day;
}
