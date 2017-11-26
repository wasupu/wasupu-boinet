package io.wasupu.boinet.population.behaviours.recurrent;

import io.wasupu.boinet.World;
import io.wasupu.boinet.population.Person;
import io.wasupu.boinet.population.behaviours.PersonBehaviour;

public class EveryDayBehaviour extends PersonBehaviour {

    public EveryDayBehaviour(World world, Person person,
                             PersonBehaviour personBehaviour) {
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

    private PersonBehaviour personBehaviour;
}
