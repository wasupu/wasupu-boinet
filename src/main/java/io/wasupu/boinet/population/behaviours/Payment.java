package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.Company;
import io.wasupu.boinet.ProductType;
import io.wasupu.boinet.World;
import io.wasupu.boinet.population.Person;
import io.wasupu.boinet.population.behaviours.GenerateRandomPrice;
import io.wasupu.boinet.population.behaviours.PersonBehaviour;

import java.math.BigDecimal;

public class Payment extends PersonBehaviour {

    public Payment(World world,
                   Person person,
                   ProductType productType,
                   Integer startPriceRange,
                   Integer endPriceRange) {
        super(world, person);

        this.productType = productType;
        this.startPriceRange = startPriceRange;
        this.endPriceRange = endPriceRange;
    }

    public Payment(World world, Person person, ProductType productType, BigDecimal price, Company company) {
        super(world, person);

        this.productType = productType;
        this.fixedPrice = price;
        this.fixedCompany = company;
    }

    public void tick() {
        BigDecimal price = (fixedPrice != null) ? fixedPrice : generateRandomPrice.apply(startPriceRange, endPriceRange);
        getCompany().buyProduct(getPerson().getPan(), productType, price);
    }

    protected Company getCompany() {
        return (fixedCompany != null) ? fixedCompany : getWorld().findCompany();
    }

    private ProductType productType;

    private BigDecimal fixedPrice;

    private Integer startPriceRange;

    private Integer endPriceRange;

    private GenerateRandomPrice generateRandomPrice = new GenerateRandomPrice();

    private Company fixedCompany;

}
