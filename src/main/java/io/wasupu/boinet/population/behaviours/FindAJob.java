package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.companies.Company;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;
import io.wasupu.boinet.population.Person;

public class FindAJob extends EconomicalSubjectBehaviour {
    public FindAJob(World world, Person person) {
        super(world, person);
    }

    @Override
    public void tick() {
        if (!((Person)getEconomicalSubject()).isUnemployed()) return;

        Company company = getWorld().findBestCompanyToWork();
        company.hire((Person)getEconomicalSubject());
    }
}
