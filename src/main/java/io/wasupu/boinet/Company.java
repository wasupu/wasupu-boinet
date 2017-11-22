package io.wasupu.boinet;

import com.google.common.collect.ImmutableMap;
import io.wasupu.boinet.persons.Person;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

public class Company {

    public Company(String identifier, World world) {
        this.identifier = identifier;
        this.world = world;

        world.listenTicks(this::tick);
    }

    public void tick() {
        contractAccount();
        initialCapital();
        hireStaff();
        paySalary();
        publishCompanyBalance();

        age++;
    }

    public String getIban() {
        return iban;
    }

    public BigDecimal getMyBalance() {
        return world.getBank().getBalance(iban);
    }

    public void buyProduct(String pan, ProductType productType, BigDecimal price) {
        world.getBank().processPayment(price, pan, iban, identifier);
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

    void hire(Person person) {
        employees.add(person);
        person.youAreHired();
    }

    private void initialCapital() {
        if (age != 0) return;

        world.getBank().deposit(iban, INITIAL_CAPITAL);
    }

    private void contractAccount() {
        if (age != 0) return;

        iban = world.getBank().contractAccount();
    }

    private void paySalary() {
        if (!isDayOfMonth(28)) return;

        employees.forEach(employee ->
            world.getBank().transfer(iban, employee.getIban(), SALARY));
    }

    private boolean isDayOfMonth(Integer dayOfMonth) {
        return dayOfMonth.equals(world.getCurrentDateTime().getDayOfMonth());
    }

    private void publishCompanyBalance() {
        if (age % 90 != 0) return;

        world.getEventPublisher().publish(STREAM_ID, ImmutableMap
            .<String, Object>builder()
            .put("company", identifier)
            .put("balance", world.getBank().getBalance(iban))
            .put("currency", "EUR")
            .put("date", world.getCurrentDateTime().toDate())
            .build());
    }

    static final BigDecimal INITIAL_CAPITAL = new BigDecimal(60000);

    private Collection<Person> employees = new ArrayList<>();

    static final BigDecimal SALARY = new BigDecimal(1000);

    private String iban;

    private String identifier;

    private World world;

    private Long age = 0L;

    private static final String STREAM_ID = "companyEventStream";

}
