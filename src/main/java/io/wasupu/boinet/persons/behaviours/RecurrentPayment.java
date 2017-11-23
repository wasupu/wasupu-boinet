package io.wasupu.boinet.persons.behaviours;

import io.wasupu.boinet.ProductType;
import io.wasupu.boinet.World;
import io.wasupu.boinet.persons.Person;

import java.math.BigDecimal;
import java.util.Random;

public abstract class RecurrentPayment extends PersonBehaviour {

    public RecurrentPayment(World world,
                            Person person,
                            ProductType productType,
                            Integer startPriceRange,
                            Integer endPriceRange) {
        super(world, person);
        this.productType = productType;
        this.startPriceRange = startPriceRange;
        this.endPriceRange = endPriceRange;
    }

    protected void executePayment() {
        getWorld().findCompany().buyProduct(getPerson().getPan(), productType, generateRandomPrice(startPriceRange, endPriceRange));
    }

    private BigDecimal generateRandomPrice(Integer startPrice, Integer endPrice) {
        Random random = new Random();
        double randomValue = startPrice + (endPrice - startPrice) * random.nextDouble();
        return new BigDecimal(randomValue)
            .setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private final ProductType productType;

    private final Integer startPriceRange;
    private final Integer endPriceRange;

}
