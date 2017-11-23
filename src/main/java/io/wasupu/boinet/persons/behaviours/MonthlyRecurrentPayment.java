package io.wasupu.boinet.persons.behaviours;

import io.wasupu.boinet.Company;
import io.wasupu.boinet.ProductType;
import io.wasupu.boinet.World;
import io.wasupu.boinet.persons.Person;

import java.math.BigDecimal;

public class MonthlyRecurrentPayment extends RecurrentPayment {

    public MonthlyRecurrentPayment(World world,
                                   Person person,
                                   Integer day,
                                   ProductType productType,
                                   Integer startPriceRange,
                                   Integer endPriceRange,
                                   Company company) {
        super(world,
            person,
            productType,
            startPriceRange,
            endPriceRange);
        this.world = world;
        this.day = day;
        this.company = company;
    }

    public MonthlyRecurrentPayment(World world, Person person, Integer day, ProductType productType, BigDecimal price, Company company) {
        super(world, person, productType, price);
        this.world = world;
        this.day = day;
        this.company = company;
    }

    public void tick() {
        if (!isDayOfMonth(day)) return;

        executePayment();
    }

    @Override
    protected Company getCompany() {
        return company;
    }

    private boolean isDayOfMonth(Integer dayOfMonth) {
        return dayOfMonth.equals(world.getCurrentDateTime().getDayOfMonth());
    }

    private World world;

    private Integer day;

    private Company company;
}
