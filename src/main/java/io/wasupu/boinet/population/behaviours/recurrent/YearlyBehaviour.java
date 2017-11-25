package io.wasupu.boinet.population.behaviours.recurrent;

import io.wasupu.boinet.World;
import io.wasupu.boinet.population.Person;
import io.wasupu.boinet.population.behaviours.PersonBehaviour;

public class YearlyBehaviour extends PersonBehaviour {

    public YearlyBehaviour(World world,
                           Person person,
                           Integer day,
                           PersonBehaviour personBehaviour) {
        super(world, person);
        this.day = day;
        this.personBehaviour = personBehaviour;
    }

    public void tick() {
        if (!isDayOfTheYear()) return;

        personBehaviour.tick();
    }

    private boolean isDayOfTheYear() {
        return getWorld().getCurrentDateTime().getDayOfYear() == day;
    }

    private PersonBehaviour personBehaviour;

    private int day;
}
