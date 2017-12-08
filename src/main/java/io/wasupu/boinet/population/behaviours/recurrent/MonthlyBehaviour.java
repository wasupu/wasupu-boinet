package io.wasupu.boinet.population.behaviours.recurrent;

import io.wasupu.boinet.World;
import io.wasupu.boinet.population.Person;
import io.wasupu.boinet.population.behaviours.EconomicalSubjectBehaviour;

public class MonthlyBehaviour extends EconomicalSubjectBehaviour {

    public MonthlyBehaviour(World world,
                            Person person,
                            Integer day,
                            EconomicalSubjectBehaviour personBehaviour) {
        super(world,
            person);

        this.day = day;
        this.personBehaviour = personBehaviour;
    }

    public void tick() {
        if (!isDayOfMonth(day)) return;

        personBehaviour.tick();
    }


    private boolean isDayOfMonth(Integer dayOfMonth) {
        return dayOfMonth.equals(getWorld().getCurrentDateTime().getDayOfMonth());
    }

    private Integer day;

    private EconomicalSubjectBehaviour personBehaviour;
}
