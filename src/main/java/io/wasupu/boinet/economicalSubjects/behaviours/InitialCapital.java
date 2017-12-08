package io.wasupu.boinet.economicalSubjects.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.EconomicalSubject;

import java.math.BigDecimal;

public class InitialCapital extends EconomicalSubjectBehaviour {

    public InitialCapital(World world, EconomicalSubject economicalSubject, BigDecimal initialCapital) {
        super(world, economicalSubject);
        this.initialCapital = initialCapital;
    }

    public void tick() {
        if (getEconomicalSubject().getAge() != 0) return;

        getWorld().getBank().deposit(getEconomicalSubject().getIban(), initialCapital);
    }

    private final BigDecimal initialCapital;
}
