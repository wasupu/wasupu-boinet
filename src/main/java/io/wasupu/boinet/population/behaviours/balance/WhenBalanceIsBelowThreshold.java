package io.wasupu.boinet.population.behaviours.balance;

import io.wasupu.boinet.World;
import io.wasupu.boinet.population.Person;
import io.wasupu.boinet.population.behaviours.PersonBehaviour;

import java.math.BigDecimal;

public class WhenBalanceIsBelowThreshold extends PersonBehaviour {
    public WhenBalanceIsBelowThreshold(World world,
                                       Person person,
                                       BigDecimal threshold,
                                       PersonBehaviour personBehaviour) {
        super(world, person);
        this.threshold = threshold;
        this.personBehaviour = personBehaviour;
    }

    @Override
    public void tick() {
        if (threshold.compareTo(getWorld().getBank().getBalance(getPerson().getIban())) > 0) return;

        personBehaviour.tick();
    }

    private BigDecimal threshold;

    private PersonBehaviour personBehaviour;

}
