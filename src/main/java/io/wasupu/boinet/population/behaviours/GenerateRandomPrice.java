package io.wasupu.boinet.population.behaviours;

import java.math.BigDecimal;
import java.util.Random;
import java.util.function.BiFunction;

public class GenerateRandomPrice implements BiFunction<Integer, Integer, BigDecimal> {

    @Override
    public BigDecimal apply(Integer startPrice, Integer endPrice) {
        Random random = new Random();
        double randomValue = startPrice + (endPrice - startPrice) * random.nextDouble();
        return new BigDecimal(randomValue)
            .setScale(2, BigDecimal.ROUND_HALF_UP);
    }

}