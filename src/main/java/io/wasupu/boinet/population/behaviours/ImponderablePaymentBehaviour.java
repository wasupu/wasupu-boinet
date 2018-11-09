package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.companies.Company;
import io.wasupu.boinet.companies.ProductType;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;
import io.wasupu.boinet.population.Person;

import java.util.Random;

public class ImponderablePaymentBehaviour extends EconomicalSubjectBehaviour {

    public ImponderablePaymentBehaviour(World world,
                                        Person person,
                                        ProductType productType,
                                        Integer startPriceRange,
                                        Integer endPriceRange,
                                        double probability) {
        super(world, person);
        this.productType = productType;
        this.startPriceRange = startPriceRange;
        this.endPriceRange = endPriceRange;
        this.probability = probability;
    }

    @Override
    public void tick() {
        if (!fulfillsTheProbability()) return;

        getCompany().buyProduct( ((Person)getEconomicalSubject()).getPan(), productType, generateRandomPrice.apply(startPriceRange, endPriceRange));
    }

    private boolean fulfillsTheProbability(){
        return getRandomValue() <= probability;
    }

    private double getRandomValue() {
        return new Random().nextDouble()*100;
    }

    private Company getCompany() {
        return getWorld().findCompany();
    }

    private ProductType productType;

    private Integer startPriceRange;

    private Integer endPriceRange;

    private GenerateRandomPrice generateRandomPrice = new GenerateRandomPrice();

    private final double probability;
}
