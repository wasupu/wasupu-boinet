package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.population.Person;

import java.math.BigDecimal;

public class StopWhenBalanceIsLessThreshold extends PersonBehaviour {
    public StopWhenBalanceIsLessThreshold(World world,
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
