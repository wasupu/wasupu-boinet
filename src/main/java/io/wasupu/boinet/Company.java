package io.wasupu.boinet;

import com.github.javafaker.Address;
import com.github.javafaker.Faker;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.wasupu.boinet.population.Person;
import io.wasupu.boinet.population.behaviours.GenerateRandomPrice;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Company {

    public Company(String identifier, World world) {
        this.identifier = identifier;
        this.name = faker.company().name();
        this.address = faker.address();
        this.world = world;

        Pair<Double, Double> coordinates = this.world.getGPS().coordinates();
        this.coordinates = coordinates;
        this.latitude = coordinates.getLeft().toString();
        this.longitude = coordinates.getRight().toString();

        world.listenTicks(this::tick);
    }

    public void tick() {
        contractAccount();
        initialCapital();
        paySalary();
        payBonus();
        publishCompanyBalance();

        age++;
    }

    public String getIban() {
        return iban;
    }

    public void buyProduct(String pan, ProductType productType, BigDecimal price) {
        world.getBank().processPayment(price,
            pan,
            iban,
            identifier,
            productType.toString().toLowerCase(),
            world.getGPS().coordinatesAround(coordinates.getLeft(), coordinates.getRight()));
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

    BigDecimal getEmployeeSalary(Person person) {
        return employees.get(person);
    }

    public void hire(Person person) {
        employees.put(person, generateSalary());
        person.youAreHired(this);
    }

    public void requestSalaryRevision(Person person) {
        if (world.getBank().getBalance(iban).compareTo(new BigDecimal(6000)) < 0) return;

        BigDecimal salary = employees.get(person);

        BigDecimal newSalary = salary.add(salary.multiply(new BigDecimal(0.2)))
            .setScale(2,RoundingMode.CEILING);

        employees.put(person, newSalary);
    }

    private BigDecimal generateSalary() {
        return new GenerateRandomPrice().apply(1000, 2100);
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

        employees.forEach(this::payEmployee);
    }

    private void payBonus() {
        if (age < 3) return;
        if (world.getBank().getBalance(iban).compareTo(new BigDecimal("100000")) < 0) return;

        BigDecimal bonus = world.getBank().getBalance(iban)
            .subtract(new BigDecimal("60000"))
            .divide(new BigDecimal(employees.size()), RoundingMode.FLOOR).setScale(2, RoundingMode.FLOOR);

        employees.keySet().forEach(employee -> payEmployee(employee, bonus));
    }

    private void payEmployee(Person employee, BigDecimal salary) {
        world.getBank().transfer(iban, employee.getIban(), salary);
    }

    private boolean isDayOfMonth(Integer dayOfMonth) {
        return dayOfMonth.equals(world.getCurrentDateTime().getDayOfMonth());
    }

    private void publishCompanyBalance() {
        if (age % 30 != 0) return;

        world.getEventPublisher().publish(STREAM_ID, ImmutableMap
            .<String, Object>builder()
            .put("company", identifier)
            .put("name", name)
            .put("address", ImmutableMap.of(
                "full", address.fullAddress(),
                "zipCode", address.zipCode(),
                "geolocation", ImmutableMap.of(
                    "latitude", latitude,
                    "longitude", longitude)))
            .put("balance", world.getBank().getBalance(iban))
            .put("currency", "EUR")
            .put("date", world.getCurrentDateTime().toDate())
            .put("eventType", "companyBalance")
            .build());
    }

    static final BigDecimal INITIAL_CAPITAL = new BigDecimal("60000");

    private Map<Person, BigDecimal> employees = new ConcurrentHashMap<>();

    private String iban;

    private String identifier;

    private String name;
    private String latitude;
    private String longitude;

    private Address address;

    private World world;

    private Long age = 0L;

    private static final String STREAM_ID = "companyEventStream";

    private static final Faker faker = new Faker();

    private final Pair<Double, Double> coordinates;


}
