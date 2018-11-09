package io.wasupu.boinet.economicalSubjects.behaviours.recurrent;

import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;
import io.wasupu.boinet.population.Person;

public class EveryDayBehaviour extends EconomicalSubjectBehaviour {

    public EveryDayBehaviour(World world, Person person,
                             EconomicalSubjectBehaviour personBehaviour) {
        super(world,
            person);
        this.person = person;
        this.personBehaviour = personBehaviour;
    }

    public void tick() {
        if (person.getAge() < 2) return;

        personBehaviour.tick();
    }

    private Person person;

    private EconomicalSubjectBehaviour personBehaviour;
}
