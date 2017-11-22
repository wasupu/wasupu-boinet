package io.wasupu.boinet.persons.behaviours;

import io.wasupu.boinet.ProductType;
import io.wasupu.boinet.World;
import io.wasupu.boinet.persons.Person;

import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class GoToCountryside {


    public GoToCountryside(World world, Person person) {
        this.world = world;
        this.person = person;
    }

    public void tick() {
        if (iHaveLessThan(new BigDecimal("1000"))) {
            iWasGoingToCountryside.set(false);
            return;
        }

        if (!iWasGoingToCountryside.get() && !iHaveMoreThan(new BigDecimal("6000"))) return;

        iWasGoingToCountryside.set(true);

        if (!isWeekend()) return;

        world.findCompany().buyProduct(person.getPan(), ProductType.ENTERTAINMENT, generateRandomPrice(100, 500));

    }

    private boolean isWeekend() {
        return world.getCurrentDateTime().getDayOfWeek() > 5;
    }

    private boolean iHaveLessThan(BigDecimal expectedThreshold) {
        return expectedThreshold.compareTo(world.getBank().getBalance(person.getIban())) >= 0;
    }

    private boolean iHaveMoreThan(BigDecimal expectedThreshold) {
        return expectedThreshold.compareTo(world.getBank().getBalance(person.getIban())) < 0;
    }

    private BigDecimal generateRandomPrice(Integer startPrice, Integer endPrice) {
        Random random = new Random();
        double randomValue = startPrice + (endPrice - startPrice) * random.nextDouble();
        return new BigDecimal(randomValue)
            .setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private AtomicBoolean iWasGoingToCountryside = new AtomicBoolean(false);

    private World world;

    private Person person;
}
