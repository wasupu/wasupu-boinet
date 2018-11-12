package io.wasupu.boinet.economicalSubjects.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.EconomicalSubject;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;
import io.wasupu.boinet.population.Person;

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
