package io.wasupu.boinet.economicalSubjects.behaviours.recurrent;

import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;
import io.wasupu.boinet.population.Person;

public class MonthlyBehaviour extends EconomicalSubjectBehaviour {

    public MonthlyBehaviour(World world,
                            Person person,
                            Integer day,
                            EconomicalSubjectBehaviour economicalSubjectBehaviour) {
        super(world,
            person);

        this.day = day;
        this.economicalSubjectBehaviour = economicalSubjectBehaviour;
    }

    public void tick() {
        if (!isDayOfMonth(day)) return;

        economicalSubjectBehaviour.tick();
    }

    @Override
    public String getIdentifier() {
        return economicalSubjectBehaviour.getIdentifier();
    }

    private boolean isDayOfMonth(Integer dayOfMonth) {
        return dayOfMonth.equals(getWorld().getCurrentDateTime().getDayOfMonth());
    }

    private Integer day;

    private EconomicalSubjectBehaviour economicalSubjectBehaviour;
}
