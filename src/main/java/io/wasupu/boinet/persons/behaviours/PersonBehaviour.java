package io.wasupu.boinet.persons.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.persons.Person;

public abstract class PersonBehaviour {

    public PersonBehaviour(World world, Person person) {
        this.world = world;
        this.person = person;
    }

    public abstract void tick();

    public World getWorld() {
        return world;
    }

    public Person getPerson() {
        return person;
    }

    private World world;

    private Person person;
}
