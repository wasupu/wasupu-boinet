package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;
import io.wasupu.boinet.population.Person;

import java.math.BigDecimal;

public class PayMortgage extends EconomicalSubjectBehaviour {

    public PayMortgage(World world, Person person, BigDecimal amortization) {
        super(world, person);

        this.amortization = amortization;
    }

    public void tick() {
        getWorld().getBank().payMortgage(((Person) getEconomicalSubject()).getMortgageIdentifier(), amortization);
    }

    private BigDecimal amortization;
}
