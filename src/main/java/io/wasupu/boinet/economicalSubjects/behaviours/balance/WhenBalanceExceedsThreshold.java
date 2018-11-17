package io.wasupu.boinet.economicalSubjects.behaviours.balance;

import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.EconomicalSubject;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;
import io.wasupu.boinet.population.Person;
import io.wasupu.boinet.subjects.Behaviour;

import java.math.BigDecimal;

public class WhenBalanceExceedsThreshold extends EconomicalSubjectBehaviour {

    public WhenBalanceExceedsThreshold(World world,
                                       EconomicalSubject economicalSubject,
                                       BigDecimal threshold,
                                       Behaviour economicalSubjectBehaviour) {
        super(world, economicalSubject);
        this.threshold = threshold;
        this.economicalSubjectBehaviour = economicalSubjectBehaviour;
    }

    @Override
    public void tick() {
        if (threshold.compareTo(getWorld().getBank().getBalance(getEconomicalSubject().getIban())) > 0) return;

        economicalSubjectBehaviour.tick();
    }

    @Override
    public String getIdentifier() {
        return economicalSubjectBehaviour.getIdentifier();
    }

    private BigDecimal threshold;

    private Behaviour economicalSubjectBehaviour;

}
