package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.population.Person;

public class RequestSalaryRevisionYearly extends PersonBehaviour {

    public RequestSalaryRevisionYearly(World world, Person person, int dayInAYear) {
        super(world, person);
        this.dayOfYear = dayInAYear;
    }

    @Override
    public void tick() {
        if (getPerson().getAge() < 2) return;
        if (getWorld().getCurrentDateTime().getDayOfYear() != dayOfYear) return;

        getPerson().getEmployer().requestSalaryRevision(getPerson());
    }

    private int dayOfYear;
}
