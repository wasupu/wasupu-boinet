package io.wasupu.boinet.economicalSubjects.behaviours;

import io.wasupu.boinet.economicalSubjects.EconomicalSubject;
import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;

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
