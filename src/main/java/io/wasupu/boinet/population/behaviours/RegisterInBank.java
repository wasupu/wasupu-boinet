package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;
import io.wasupu.boinet.population.Person;

public class RegisterInBank extends EconomicalSubjectBehaviour {

    public RegisterInBank(World world, Person person) {
        super(world, person);
    }

    @Override
    public void tick() {
        var person = (Person) getEconomicalSubject();
        if (person.getAge() > 0) return;

        getWorld().getBank().registerUser(person);
    }
}
