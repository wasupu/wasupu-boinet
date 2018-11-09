package io.wasupu.boinet.companies;

import com.github.javafaker.Address;
import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.EconomicalSubject;
import io.wasupu.boinet.population.Person;
import io.wasupu.boinet.population.behaviours.GenerateRandomPrice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Company extends EconomicalSubject {

    public Company(String identifier, World world) {
        super(identifier, world);

        this.name = faker.company().name();
        Address addressFaker = faker.address();
        this.fullAddress = addressFaker.fullAddress();
        this.zipCode = addressFaker.zipCode();
    }

    public void tick() {
        paySalary();
        payBonus();

        super.tick();
    }

    public void buyProduct(String pan, ProductType productType, BigDecimal price) {
        getWorld().getBank().processPayment(price,
            pan,
            getIban(),
            getIdentifier(),
            productType.toString().toLowerCase(),
            getWorld().getGPS().coordinatesAround(getCoordinates().getLeft(),
                getCoordinates().getRight()));
    }

    BigDecimal getEmployeeSalary(Person person) {
        return employees.get(person);
    }

    public void hire(Person person) {
        employees.put(person, generateSalary());
        person.youAreHired(this);
    }

    public int getNumberOfEmployees() {
        return employees.size();
    }

    private void fire(Person person) {
        employees.remove(person);
        person.youAreFired();
    }

    public void requestSalaryRevision(Person person) {
        if (getWorld().getBank().getBalance(getIban()).compareTo(new BigDecimal(6000)) < 0) return;

        BigDecimal salary = employees.get(person);

        BigDecimal newSalary = salary.add(salary.multiply(new BigDecimal(0.2)))
            .setScale(2, RoundingMode.CEILING);

        employees.put(person, newSalary);
    }

    private BigDecimal generateSalary() {
        return new GenerateRandomPrice().apply(1200, 2300);
    }

    private void paySalary() {
        if (!isDayOfMonth(27)) return;

        employees.forEach(this::payEmployee);
    }

    private void payBonus() {
        if (getAge() < 3) return;
        if (getWorld().getBank().getBalance(getIban()).compareTo(new BigDecimal("100000")) < 0) return;

        BigDecimal bonus = getWorld().getBank().getBalance(getIban())
            .subtract(new BigDecimal("60000"))
            .divide(new BigDecimal(employees.size()), RoundingMode.FLOOR).setScale(2, RoundingMode.FLOOR);

        employees.keySet().forEach(employee -> payEmployee(employee, bonus));
    }

    private void payEmployee(Person employee, BigDecimal salary) {
        if (getWorld().getBank().getBalance(getIban()).compareTo(salary) < 0) {
            fire(employee);
        }

        getWorld().getBank().transfer(getIban(), employee.getIban(), salary);
    }

    private boolean isDayOfMonth(Integer dayOfMonth) {
        return dayOfMonth.equals(getWorld().getCurrentDateTime().getDayOfMonth());
    }


    private Map<Person, BigDecimal> employees = new ConcurrentHashMap<>();

    private String name;

    private String zipCode;

    private String fullAddress;
}
