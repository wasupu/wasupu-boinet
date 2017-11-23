package io.wasupu.boinet.persons.behaviours;

import java.math.BigDecimal;
import java.util.Random;

public class GenerateRandomPrice {

    public BigDecimal generateRandomPrice(Integer startPrice, Integer endPrice) {
        Random random = new Random();
        double randomValue = startPrice + (endPrice - startPrice) * random.nextDouble();
        return new BigDecimal(randomValue)
            .setScale(2, BigDecimal.ROUND_HALF_UP);
    }

}