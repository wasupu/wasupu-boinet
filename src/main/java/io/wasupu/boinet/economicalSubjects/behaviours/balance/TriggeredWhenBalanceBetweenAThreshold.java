package io.wasupu.boinet.economicalSubjects.behaviours.balance;

import io.wasupu.boinet.World;
import io.wasupu.boinet.population.Person;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;

public class TriggeredWhenBalanceBetweenAThreshold extends EconomicalSubjectBehaviour {

    public TriggeredWhenBalanceBetweenAThreshold(World world,
                                                 Person person,
                                                 BigDecimal lowerThreshold,
                                                 BigDecimal upperThreshold,
                                                 EconomicalSubjectBehaviour personBehaviour) {
        super(world, person);

        this.personBehaviour = personBehaviour;
        this.lowerThreshold = lowerThreshold;
        this.upperThreshold = upperThreshold;
    }

    public void tick() {
        if (iHaveLessThan(lowerThreshold)) {
            iWasGoingToCountryside.set(false);
            return;
        }

        if (!iWasGoingToCountryside.get() && !iHaveMoreThan(upperThreshold)) return;

        iWasGoingToCountryside.set(true);
        personBehaviour.tick();
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


    private EconomicalSubjectBehaviour personBehaviour;

    private BigDecimal lowerThreshold;
    private BigDecimal upperThreshold;

}
