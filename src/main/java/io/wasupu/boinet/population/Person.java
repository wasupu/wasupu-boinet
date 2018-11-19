package io.wasupu.boinet.population;

import io.wasupu.boinet.World;
import io.wasupu.boinet.companies.Company;
import io.wasupu.boinet.economicalSubjects.EconomicalSubject;
import io.wasupu.boinet.economicalSubjects.EconomicalSubjectType;

import java.math.BigDecimal;

import static io.wasupu.boinet.economicalSubjects.EconomicalSubjectType.PERSON;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class Person extends EconomicalSubject {

    public Person(String identifier, World world) {
        super(identifier, world);

        this.name = faker.name().fullName();
    }

    @Override
    public String getName() {
        return name;
    }

    public Boolean isUnemployed() {
        return !employed;
    }

    public void youAreHired(Company company) {
        this.company = company;
        employed = TRUE;
    }

    public void youAreFired() {
        this.company = null;
        employed = FALSE;
    }

    public Boolean hasEnoughMoney() {
        return getWorld().getBank()
            .getLastMonthDifferenceBetweenIncomeAndExpenses(getIban()).compareTo(new BigDecimal("0")) > 0;
    }

    public BigDecimal getDifferenceBetweenIncomeAndExpenses(){
        return getWorld().getBank().getLastMonthDifferenceBetweenIncomeAndExpenses(getIban());
    }

    @Override
    public EconomicalSubjectType getType() {
        return PERSON;
    }

    public void setMortgageIdentifier(String mortgageIdentifier) {
        this.mortgageIdentifier = mortgageIdentifier;
    }

    public String getPan() {
        return pan;
    }

    public String getMortgageIdentifier() {
        return mortgageIdentifier;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public Company getEmployer() {
        return company;
    }

    private String pan;

    private Boolean employed = FALSE;

    private String name;

    private Company company;

    private String mortgageIdentifier;
}
