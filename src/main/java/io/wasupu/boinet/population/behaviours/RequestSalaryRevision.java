package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;
import io.wasupu.boinet.population.Person;

public class RequestSalaryRevision extends EconomicalSubjectBehaviour {

    public RequestSalaryRevision(World world, Person person) {
        super(world, person);
    }

    @Override
    public void tick() {
        if (getEconomicalSubject().getAge() < 2) return;

        var person = (Person) getEconomicalSubject();
        if (person.isUnemployed()) return;
        if (person.hasEnoughMoney()) return;

        person.getEmployer().requestSalaryRevision(person);
    }
}
