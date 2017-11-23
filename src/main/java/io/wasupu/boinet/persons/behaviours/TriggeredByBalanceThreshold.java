package io.wasupu.boinet.persons.behaviours;

import io.wasupu.boinet.ProductType;
import io.wasupu.boinet.World;
import io.wasupu.boinet.persons.Person;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;

public class TriggeredByBalanceThreshold {

    public TriggeredByBalanceThreshold(World world, Person person) {
        this.world = world;
        this.person = person;

        weekendRecurrentPayment = new WeekendRecurrentPayment(world,
            person,
            ProductType.ENTERTAINMENT,
            100,
            500);
    }

    public void tick() {
        if (iHaveLessThan(new BigDecimal("1000"))) {
            iWasGoingToCountryside.set(false);
            return;
        }

        if (!iWasGoingToCountryside.get() && !iHaveMoreThan(new BigDecimal("6000"))) return;

        iWasGoingToCountryside.set(true);

        weekendRecurrentPayment.tick();
    }

    private boolean iHaveLessThan(BigDecimal expectedThreshold) {
        return expectedThreshold.compareTo(world.getBank().getBalance(person.getIban())) >= 0;
    }

    private boolean iHaveMoreThan(BigDecimal expectedThreshold) {
        return expectedThreshold.compareTo(world.getBank().getBalance(person.getIban())) < 0;
    }

    private AtomicBoolean iWasGoingToCountryside = new AtomicBoolean(false);

    private World world;

    private Person person;

    private WeekendRecurrentPayment weekendRecurrentPayment;
}
