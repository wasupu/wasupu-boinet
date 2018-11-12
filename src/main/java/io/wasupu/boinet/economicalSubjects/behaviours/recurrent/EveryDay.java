package io.wasupu.boinet.economicalSubjects.behaviours.recurrent;

import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;
import io.wasupu.boinet.population.Person;

public class EveryDay extends EconomicalSubjectBehaviour {

    public EveryDay(World world, Person person,
                    EconomicalSubjectBehaviour economicalSubjectBehaviour) {
        super(world,
            person);
        this.person = person;
        this.economicalSubjectBehaviour = economicalSubjectBehaviour;
    }

    public void tick() {
        if (person.getAge() < 2) return;

        economicalSubjectBehaviour.tick();
    }

    @Override
    public String getIdentifier() {
        return economicalSubjectBehaviour.getIdentifier();
    }

    private Person person;

    private EconomicalSubjectBehaviour economicalSubjectBehaviour;
}
