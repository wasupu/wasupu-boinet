package io.wasupu.boinet.persons.behaviours;

import io.wasupu.boinet.ProductType;
import io.wasupu.boinet.World;
import io.wasupu.boinet.persons.Person;

import java.math.BigDecimal;
import java.util.Random;

public class MonthyRecurrentPaymentBehaviour {

    public MonthyRecurrentPaymentBehaviour(World world,
                                           Person person,
                                           Integer day,
                                           ProductType productType,
                                           Integer startPriceRange,
                                           Integer endPriceRange) {
        this.world = world;
        this.person = person;
        this.day = day;
        this.productType = productType;
        this.startPriceRange = startPriceRange;
        this.endPriceRange = endPriceRange;
    }

    public void tick() {
        if (!isDayOfMonth(day)) return;

        world.findCompany().buyProduct(person.getPan(), productType, generateRandomPrice(startPriceRange, endPriceRange));
    }

    private boolean isDayOfMonth(Integer dayOfMonth) {
        return dayOfMonth.equals(world.getCurrentDateTime().getDayOfMonth());
    }

    private BigDecimal generateRandomPrice(Integer startPrice, Integer endPrice) {
        Random random = new Random();
        double randomValue = startPrice + (endPrice - startPrice) * random.nextDouble();
        return new BigDecimal(randomValue)
            .setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private World world;

    private Person person;

    private Integer day;

    private final ProductType productType;

    private final Integer startPriceRange;
    private final Integer endPriceRange;

}
