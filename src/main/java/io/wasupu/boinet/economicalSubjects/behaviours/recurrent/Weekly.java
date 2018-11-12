package io.wasupu.boinet.economicalSubjects.behaviours.recurrent;

import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;
import io.wasupu.boinet.population.Person;

public class Weekly extends EconomicalSubjectBehaviour {

    public Weekly(World world,
                  Person person,
                  Integer day, EconomicalSubjectBehaviour economicalSubjectBehaviour) {
        super(world, person);
        this.day = day;
        this.economicalSubjectBehaviour = economicalSubjectBehaviour;
    }

    public void tick() {
        if (!isDayInTheWeek()) return;

        economicalSubjectBehaviour.tick();
    }

    @Override
    public String getIdentifier() {
        return economicalSubjectBehaviour.getIdentifier();
    }

    private boolean isDayInTheWeek() {
        return getWorld().getCurrentDateTime().getDayOfWeek() == day;
    }

    private int day;

    private EconomicalSubjectBehaviour economicalSubjectBehaviour;
}
