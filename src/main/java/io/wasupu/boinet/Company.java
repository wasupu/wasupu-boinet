package io.wasupu.boinet;

import com.github.javafaker.Address;
import com.github.javafaker.Faker;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.wasupu.boinet.population.Person;
import io.wasupu.boinet.population.behaviours.GenerateRandomPrice;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Company {

    public Company(String identifier, World world) {
        this.identifier = identifier;
        this.name = faker.company().name();
        this.address = faker.address();
        this.world = world;

        Pair<Double,Double> coordinates = this.world.getGPS().coordinates();
        this.coordinates = coordinates;
        this.latitude = coordinates.getLeft().toString();
        this.longitude = coordinates.getRight().toString();

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
        world.getBank().processPayment(price,
            pan,
            iban,
            identifier,
            productType.toString().toLowerCase(),
            world.getGPS().coordinatesAround(coordinates.getLeft(),coordinates.getRight()));
    }

    public Collection<Person> getEmployees() {
        return ImmutableList.copyOf(employees.keySet());
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

    private void hireStaff() {
        if (age != 1) return;

        world.getCandidates(INITIAL_CAPITAL)
            .forEach(this::hire);
    }

    void hire(Person person) {
        employees.put(person, generateSalary());
        person.youAreHired();
    }

    private BigDecimal generateSalary() {
        return new GenerateRandomPrice().apply(700, 2000);
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

        employees.forEach((employee, salary) ->
            world.getBank().transfer(iban, employee.getIban(), salary));
    }

    private boolean isDayOfMonth(Integer dayOfMonth) {
        return dayOfMonth.equals(world.getCurrentDateTime().getDayOfMonth());
    }

    private void publishCompanyBalance() {
        if (age % 90 != 0) return;

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
            .build());
    }

    static final BigDecimal INITIAL_CAPITAL = new BigDecimal(60000);

    private Map<Person, BigDecimal> employees = new HashMap<>();

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
