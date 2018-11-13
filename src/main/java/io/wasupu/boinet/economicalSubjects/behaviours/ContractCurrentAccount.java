package io.wasupu.boinet.economicalSubjects.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.EconomicalSubject;

public class ContractCurrentAccount extends EconomicalSubjectBehaviour {

    public ContractCurrentAccount(World world, EconomicalSubject economicalSubject) {
        super(world, economicalSubject);
    }

    public void tick() {
        if (getEconomicalSubject().getAge() != 0) return;

        getEconomicalSubject().setIban(getWorld().getBank().contractCurrentAccount(getEconomicalSubject().getIdentifier()));
    }
}
