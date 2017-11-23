package io.wasupu.boinet.persons.behaviours;

import io.wasupu.boinet.ProductType;
import io.wasupu.boinet.World;
import io.wasupu.boinet.persons.Person;

import java.math.BigDecimal;
import java.util.Random;

public class MonthlyRecurrentPayment extends RecurrentPayment{

    public MonthlyRecurrentPayment(World world,
                                   Person person,
                                   Integer day,
                                   ProductType productType,
                                   Integer startPriceRange,
                                   Integer endPriceRange) {
        super(world,
            person,
            productType,
            startPriceRange,
            endPriceRange);
        this.world = world;
        this.day = day;
    }

    public void tick() {
        if (!isDayOfMonth(day)) return;

        executePayment();
    }

    private boolean isDayOfMonth(Integer dayOfMonth) {
        return dayOfMonth.equals(world.getCurrentDateTime().getDayOfMonth());
    }

    private World world;

    private Integer day;
}
