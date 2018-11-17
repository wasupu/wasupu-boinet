package io.wasupu.boinet.companies.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.companies.Company;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PayBonus extends EconomicalSubjectBehaviour {

    public PayBonus(World world, Company company) {
        super(world, company);

        this.company = company;
    }

    @Override
    public void tick() {
        if (company.getAge() < 3) return;
        if (getWorld().getBank().getBalance(company.getIban()).compareTo(new BigDecimal("100000")) < 0) return;

        var bonus = getWorld().getBank().getBalance(company.getIban())
            .subtract(new BigDecimal("60000"))
            .divide(new BigDecimal(company.getEmployees().size()), RoundingMode.FLOOR).setScale(2, RoundingMode.FLOOR);

        company.getEmployees().keySet().forEach(employee -> company.payEmployee(employee, bonus));
    }


    private Company company;
}
