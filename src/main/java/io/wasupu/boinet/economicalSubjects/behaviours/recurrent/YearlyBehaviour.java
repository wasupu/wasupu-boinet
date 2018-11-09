package io.wasupu.boinet.economicalSubjects.behaviours.recurrent;

import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;
import io.wasupu.boinet.population.Person;

public class YearlyBehaviour extends EconomicalSubjectBehaviour {

    public YearlyBehaviour(World world,
                           Person person,
                           Integer day,
                           EconomicalSubjectBehaviour personBehaviour) {
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

    private EconomicalSubjectBehaviour personBehaviour;

    private int day;
}
