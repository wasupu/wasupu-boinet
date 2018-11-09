package io.wasupu.boinet.economicalSubjects.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.EconomicalSubject;

public class ContractAccount extends EconomicalSubjectBehaviour {

    public ContractAccount(World world, EconomicalSubject economicalSubject) {
        super(world, economicalSubject);
    }

    public void tick() {
        if (getEconomicalSubject().getAge() != 0) return;

        getEconomicalSubject().setIban(getWorld().getBank().contractAccount(getEconomicalSubject().getIdentifier()));
    }
}
