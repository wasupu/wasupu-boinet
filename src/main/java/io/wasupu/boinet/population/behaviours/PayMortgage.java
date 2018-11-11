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
        var bank = getWorld().getBank();
        var person = (Person) getEconomicalSubject();
        var mortgageId = person.getMortgageIdentifier();

        bank.payMortgage(mortgageId, amortization);

        if (bank.isMortgageAmortized(mortgageId)){
            bank.cancelMortgage(mortgageId);
            person.omitTicks(this);
        }
    }

    private BigDecimal amortization;
}
