package io.wasupu.boinet.economicalSubjects.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.EconomicalSubject;

public class RegisterInBank extends EconomicalSubjectBehaviour {

    public RegisterInBank(World world, EconomicalSubject person) {
        super(world, person);
    }

    @Override
    public void tick() {
        var subject = getEconomicalSubject();
        if (subject.getAge() > 0) return;

        getWorld().getBank().registerUser(subject);
    }
}
