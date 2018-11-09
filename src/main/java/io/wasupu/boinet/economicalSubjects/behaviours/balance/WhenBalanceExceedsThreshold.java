package io.wasupu.boinet.economicalSubjects.behaviours.balance;

import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;
import io.wasupu.boinet.population.Person;

import java.math.BigDecimal;

public class WhenBalanceExceedsThreshold extends EconomicalSubjectBehaviour {

    public WhenBalanceExceedsThreshold(World world,
                                       Person person,
                                       BigDecimal threshold,
                                       EconomicalSubjectBehaviour personBehaviour) {
        super(world, person);
        this.threshold = threshold;
        this.personBehaviour = personBehaviour;
    }

    @Override
    public void tick() {
        if (threshold.compareTo(getWorld().getBank().getBalance(getEconomicalSubject().getIban())) > 0) return;

        personBehaviour.tick();
    }

    private BigDecimal threshold;

    private EconomicalSubjectBehaviour personBehaviour;

}
