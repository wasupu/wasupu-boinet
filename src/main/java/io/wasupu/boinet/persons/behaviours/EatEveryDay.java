package io.wasupu.boinet.persons.behaviours;

import io.wasupu.boinet.ProductType;
import io.wasupu.boinet.World;
import io.wasupu.boinet.persons.Person;

import java.math.BigDecimal;
import java.util.Random;

public class EatEveryDay {

    public EatEveryDay(World world, Person person) {
        this.world = world;
        this.person = person;
    }

    public void tick() {
        if (person.getAge() < 2) return;

        world.findCompany().buyProduct(person.getPan(), ProductType.MEAL, generateRandomPrice(10, 20));
    }

    private BigDecimal generateRandomPrice(Integer startPrice, Integer endPrice) {
        Random random = new Random();
        double randomValue = startPrice + (endPrice - startPrice) * random.nextDouble();
        return new BigDecimal(randomValue)
            .setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private World world;

    private Person person;
}
