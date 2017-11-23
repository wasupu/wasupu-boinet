package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.Company;
import io.wasupu.boinet.ProductType;
import io.wasupu.boinet.World;
import io.wasupu.boinet.population.Person;

import java.math.BigDecimal;

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

    public RecurrentPayment(World world, Person person, ProductType productType, BigDecimal price) {
        super(world, person);
        this.productType = productType;
        this.fixedPrice = price;
    }

    protected void executePayment() {
        BigDecimal price = (fixedPrice != null) ? fixedPrice : generateRandomPrice.generateRandomPrice(startPriceRange, endPriceRange);
        getCompany().buyProduct(getPerson().getPan(), productType, price);
    }

    protected Company getCompany() {
        return getWorld().findCompany();
    }


    private ProductType productType;

    private BigDecimal fixedPrice;

    private Integer startPriceRange;

    private Integer endPriceRange;

    private GenerateRandomPrice generateRandomPrice = new GenerateRandomPrice();

}
