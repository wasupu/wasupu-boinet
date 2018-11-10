package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.companies.Company;
import io.wasupu.boinet.companies.ProductType;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;
import io.wasupu.boinet.population.Person;

import java.math.BigDecimal;

public class MakeAPaymentWithCard extends EconomicalSubjectBehaviour {

    public MakeAPaymentWithCard(World world,
                                Person person,
                                ProductType productType,
                                Integer startPriceRange,
                                Integer endPriceRange) {
        super(world, person);

        this.productType = productType;
        this.startPriceRange = startPriceRange;
        this.endPriceRange = endPriceRange;
    }

    public MakeAPaymentWithCard(World world,
                                Person person,
                                ProductType productType,
                                BigDecimal price,
                                Company company) {
        super(world, person);

        this.productType = productType;
        this.fixedPrice = price;
        this.fixedCompany = company;
    }

    public void tick() {
        var price = (fixedPrice != null) ? fixedPrice : generateRandomPrice.apply(startPriceRange, endPriceRange);

        getCompany().buyProduct(((Person) getEconomicalSubject()).getPan(), productType, price);
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
