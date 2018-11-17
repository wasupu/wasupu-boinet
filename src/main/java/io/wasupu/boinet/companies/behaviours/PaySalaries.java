package io.wasupu.boinet.companies.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.companies.Company;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;

public class PaySalaries extends EconomicalSubjectBehaviour {

    public PaySalaries(World world, Company company) {
        super(world, company);
        this.company = company;
    }

    @Override
    public void tick() {
        company.getEmployees().forEach((person, salary) -> company.payEmployee(person, salary));
    }

    private Company company;
}
