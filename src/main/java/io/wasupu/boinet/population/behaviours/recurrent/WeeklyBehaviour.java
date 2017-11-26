package io.wasupu.boinet.population.behaviours.recurrent;

import io.wasupu.boinet.World;
import io.wasupu.boinet.population.Person;
import io.wasupu.boinet.population.behaviours.PersonBehaviour;

public class WeeklyBehaviour extends PersonBehaviour {

    public WeeklyBehaviour(World world,
                           Person person,
                           Integer day, PersonBehaviour personBehaviour) {
        super(world, person);
        this.day = day;
        this.personBehaviour = personBehaviour;
    }

    public void tick() {
        if (!isDayInTheWeek()) return;

        personBehaviour.tick();
    }

    private boolean isDayInTheWeek() {
        return getWorld().getCurrentDateTime().getDayOfWeek() == day;
    }

    private int day;

    private PersonBehaviour personBehaviour;
}
