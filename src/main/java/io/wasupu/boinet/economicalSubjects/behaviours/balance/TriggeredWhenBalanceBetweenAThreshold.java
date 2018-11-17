package io.wasupu.boinet.economicalSubjects.behaviours.balance;

import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.EconomicalSubject;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;
import io.wasupu.boinet.subjects.Behaviour;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;

public class TriggeredWhenBalanceBetweenAThreshold extends EconomicalSubjectBehaviour {

    public TriggeredWhenBalanceBetweenAThreshold(World world,
                                                 EconomicalSubject economicalSubject,
                                                 BigDecimal lowerThreshold,
                                                 BigDecimal upperThreshold,
                                                 Behaviour behaviour) {
        super(world, economicalSubject);

        this.behaviour = behaviour;
        this.lowerThreshold = lowerThreshold;
        this.upperThreshold = upperThreshold;
    }

    @Override
    public String getIdentifier() {
        return behaviour.getIdentifier();
    }

    public void tick() {
        if (iHaveLessThan(lowerThreshold)) {
            iWasGoingToCountryside.set(false);
            return;
        }

        if (!iWasGoingToCountryside.get() && !iHaveMoreThan(upperThreshold)) return;

        iWasGoingToCountryside.set(true);
        behaviour.tick();
    }

    private boolean iHaveLessThan(BigDecimal expectedThreshold) {
        return expectedThreshold.compareTo(getBalance()) >= 0;
    }

    private BigDecimal getBalance() {
        return getWorld().getBank().getBalance(getEconomicalSubject().getIban());
    }

    private boolean iHaveMoreThan(BigDecimal expectedThreshold) {
        return expectedThreshold.compareTo(getBalance()) < 0;
    }

    private AtomicBoolean iWasGoingToCountryside = new AtomicBoolean(false);


    private Behaviour behaviour;

    private BigDecimal lowerThreshold;
    private BigDecimal upperThreshold;

}
