package io.wasupu.boinet.persons.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.persons.Person;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;

public class TriggeredByBalanceThreshold {

    public TriggeredByBalanceThreshold(World world,
                                       Person person,
                                       BigDecimal lowerThreshold,
                                       BigDecimal upperThreshold,
                                       RecurrentPayment recurrentPayment) {
        this.world = world;
        this.person = person;
        this.recurrentPayment = recurrentPayment;
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

        recurrentPayment.tick();
    }

    private boolean iHaveLessThan(BigDecimal expectedThreshold) {
        return expectedThreshold.compareTo(world.getBank().getBalance(person.getIban())) >= 0;
    }

    private boolean iHaveMoreThan(BigDecimal expectedThreshold) {
        return expectedThreshold.compareTo(world.getBank().getBalance(person.getIban())) < 0;
    }

    private AtomicBoolean iWasGoingToCountryside = new AtomicBoolean(false);

    private World world;

    private Person person;

    private RecurrentPayment recurrentPayment;

    private BigDecimal lowerThreshold;
    private BigDecimal upperThreshold;

}
