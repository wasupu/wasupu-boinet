package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.Company;
import io.wasupu.boinet.World;
import io.wasupu.boinet.population.Person;

public class FindAJob extends PersonBehaviour {
    public FindAJob(World world, Person person) {
        super(world, person);
    }

    @Override
    public void tick() {
        if (getPerson().getAge() != 0) return;

        Company company = getWorld().findCompany();
        company.hire(getPerson());
    }
}
