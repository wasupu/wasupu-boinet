package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;
import io.wasupu.boinet.population.Person;

import java.math.BigDecimal;

public class RepayMortgage extends EconomicalSubjectBehaviour {

    public RepayMortgage(World world, Person person, BigDecimal amortization) {
        super(world, person);

        this.amortization = amortization;
    }

    public void tick() {
        var bank = getWorld().getBank();
        var person = (Person) getEconomicalSubject();
        var mortgageId = person.getMortgageIdentifier();

        bank.repayMortgage(mortgageId, amortization);

        if (bank.isMortgageAmortized(mortgageId)) {
            bank.cancelMortgage(mortgageId);
            person.removeBehaviour(this);
        }
    }

    private BigDecimal amortization;
}
