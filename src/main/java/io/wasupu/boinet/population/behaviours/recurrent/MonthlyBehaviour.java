package io.wasupu.boinet.population.behaviours.recurrent;

import io.wasupu.boinet.World;
import io.wasupu.boinet.population.Person;
import io.wasupu.boinet.population.behaviours.PersonBehaviour;

public class MonthlyBehaviour extends PersonBehaviour {

    public MonthlyBehaviour(World world,
                            Person person,
                            Integer day,
                            PersonBehaviour personBehaviour) {
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

    private PersonBehaviour personBehaviour;
}
