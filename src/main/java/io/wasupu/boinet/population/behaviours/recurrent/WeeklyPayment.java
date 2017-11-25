package io.wasupu.boinet.population.behaviours.recurrent;

import io.wasupu.boinet.ProductType;
import io.wasupu.boinet.World;
import io.wasupu.boinet.population.Person;

public class WeeklyPayment extends Payment {

    public WeeklyPayment(World world,
                         Person person,
                         ProductType productType,
                         Integer startPriceRange,
                         Integer endPriceRange, Integer day) {
        super(world, person, productType, startPriceRange, endPriceRange);
        this.day = day;
    }

    public void tick() {
        if (!isDayInTheWeek()) return;

        super.tick();
    }

    private boolean isDayInTheWeek() {
        return getWorld().getCurrentDateTime().getDayOfWeek() == day;
    }

    private int day;
}
