package io.wasupu.boinet.economicalSubjects.behaviours.recurrent;

import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;
import io.wasupu.boinet.population.Person;

public class WeeklyBehaviour extends EconomicalSubjectBehaviour {

    public WeeklyBehaviour(World world,
                           Person person,
                           Integer day, EconomicalSubjectBehaviour personBehaviour) {
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

    private EconomicalSubjectBehaviour personBehaviour;
}
