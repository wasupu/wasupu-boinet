package io.wasupu.boinet.economicalSubjects.behaviours.recurrent;

import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;
import io.wasupu.boinet.population.Person;

public class Yearly extends EconomicalSubjectBehaviour {

    public Yearly(World world,
                  Person person,
                  Integer day,
                  EconomicalSubjectBehaviour economicalSubjectBehaviour) {
        super(world, person);
        this.day = day;
        this.economicalSubjectBehaviour = economicalSubjectBehaviour;
    }

    @Override
    public String getIdentifier() {
        return economicalSubjectBehaviour.getIdentifier();
    }

    public void tick() {
        if (!isDayOfTheYear()) return;

        economicalSubjectBehaviour.tick();
    }

    private boolean isDayOfTheYear() {
        return getWorld().getCurrentDateTime().getDayOfYear() == day;
    }

    private EconomicalSubjectBehaviour economicalSubjectBehaviour;

    private int day;
}
