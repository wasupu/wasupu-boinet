package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;
import io.wasupu.boinet.population.Person;

public class RequestSalaryRevisionYearly extends EconomicalSubjectBehaviour {

    public RequestSalaryRevisionYearly(World world, Person person, int dayInAYear) {
        super(world, person);
        this.dayOfYear = dayInAYear;
    }

    @Override
    public void tick() {
        if (getEconomicalSubject().getAge() < 2) return;
        if (((Person) getEconomicalSubject()).isUnemployed()) return;
        if (getWorld().getCurrentDateTime().getDayOfYear() != dayOfYear) return;


        ((Person) getEconomicalSubject()).getEmployer().requestSalaryRevision((Person) getEconomicalSubject());
    }

    private int dayOfYear;
}
