package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;
import io.wasupu.boinet.population.Person;

import java.math.BigDecimal;

public class PayMortgage extends EconomicalSubjectBehaviour {

    public PayMortgage(World world, Person person, BigDecimal installmentAmount) {
        super(world, person);

        this.installmentAmount = installmentAmount;
    }

    public void tick() {
        var bank = getWorld().getBank();
        var person = (Person) getEconomicalSubject();
        var mortgageId = person.getMortgageIdentifier();

        bank.payMortgage(mortgageId, installmentAmount);

        if (bank.isMortgageAmortized(mortgageId)) {
            bank.cancelMortgage(mortgageId);
            person.removeBehaviour(this);
        }
    }

    private BigDecimal installmentAmount;
}
