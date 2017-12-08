package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.EconomicalSubject;
import io.wasupu.boinet.World;
import io.wasupu.boinet.population.Person;

import java.math.BigDecimal;

public class InitialCapital extends EconomicalSubjectBehaviour {

    public InitialCapital(World world, EconomicalSubject economicalSubject) {
        super(world, economicalSubject);
    }

    public void tick() {
        if (getEconomicalSubject().getAge() != 0) return;

        getWorld().getBank().deposit(getEconomicalSubject().getIban(), INITIAL_CAPITAL);
    }

    static final BigDecimal INITIAL_CAPITAL = new BigDecimal(3000);
}
