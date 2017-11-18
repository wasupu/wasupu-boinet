package io.wasupu.boinet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

public class Company {

    public Company(String identifier, World world) {
        this.identifier = identifier;
        this.salary = new BigDecimal(0);
        this.world = world;

        world.listenTicks(this::tick);
    }

    public void tick() {
        contractAccount();
        initialCapital(INITIAL_CAPITAL);
        hireStaff();
        age++;
    }

    public String getIban() {
        return iban;
    }

    public BigDecimal getMyBalance() {
        return world.getBank().getBalance(iban);
    }

    public void buyProduct(String pan) {
        world.getBank().processPayment(new BigDecimal(10), pan, iban, identifier);
    }

    public String getIdentifier() {
        return identifier;
    }

    public Collection<Person> getEmployees() {
        return employees;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Company company = (Company) o;

        return identifier.equals(company.identifier);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    private void hireStaff() {
        if (age != 1) return;

        world.getCandidates(INITIAL_CAPITAL)
            .forEach(this::hire);
    }

    private void hire(Person person) {
        employees.add(person);
        person.youAreHired();
    }

    private void initialCapital(BigDecimal capital) {
        if (age != 0) return;

        world.getBank().deposit(iban, capital);
    }

    private void contractAccount() {
        if (age != 0) return;

        iban = world.getBank().contractAccount();
    }

    private void paySalary() {
        employees.forEach(employee ->
            world.getBank().transfer(iban,
                employee.getIban(),
                salary));
    }

    static final BigDecimal INITIAL_CAPITAL = new BigDecimal(60000);

    private Collection<Person> employees = new ArrayList<>();

    private BigDecimal salary = new BigDecimal(1);

    private String iban;

    private String identifier;

    private World world;

    private Long age = 0L;
}
