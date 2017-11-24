package io.wasupu.boinet.population;

import com.github.javafaker.Address;
import com.github.javafaker.Faker;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.wasupu.boinet.Company;
import io.wasupu.boinet.World;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.util.Collection;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class Person {

    public Person(String identifier,
                  World world) {
        this.identifier = identifier;

        this.name = faker.name().fullName();
        this.world = world;
        this.cellPhone = faker.phoneNumber().cellPhone();
        Address address = faker.address();
        this.fullAddress = address.fullAddress();
        this.zipCode = address.zipCode();

        Pair<Double,Double> coordinates = this.world.getGPS().coordinates();
        this.latitude = coordinates.getLeft().toString();
        this.longitude = coordinates.getRight().toString();

        this.world.listenTicks(this::tick);
    }

    public void listenTicks(Runnable tickConsumer) {
        tickConsumers = ImmutableList
            .<Runnable>builder()
            .addAll(tickConsumers)
            .add(tickConsumer)
            .build();
    }

    public void tick() {
        tickConsumers.forEach(Runnable::run);

        publishPersonBalance();

        age++;
    }

    public Boolean isUnemployed() {
        return !employed;
    }

    public void youAreHired(Company company) {
        this.company = company;
        employed = TRUE;
    }

    public String getIban() {
        return iban;
    }

    public String getPan() {
        return pan;
    }

    public Long getAge() {
        return age;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    static void setFaker(Faker newFaker){
        faker = newFaker;
    }

    public Company getEmployer() {
        return company;
    }

    private void publishPersonBalance() {
        if (age % 30 != 0) return;

        world.getEventPublisher().publish(STREAM_ID, ImmutableMap
            .<String, Object>builder()
            .put("person", identifier)
            .put("name", name)
            .put("cellPhone", cellPhone)
            .put("pan", pan)
            .put("address", ImmutableMap.of(
                "full", fullAddress,
                "zipCode", zipCode,
                "geolocation", ImmutableMap.of(
                    "latitude", latitude,
                    "longitude", longitude)))
            .put("balance", world.getBank().getBalance(iban))
            .put("currency", "EUR")
            .put("date", world.getCurrentDateTime().toDate())
            .build());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        return identifier.equals(person.identifier);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    private String iban;
    private String identifier;
    private String fullAddress;
    private String zipCode;

    private String latitude;

    private String longitude;

    private String pan;

    private World world;

    private Long age = 0L;

    private Boolean employed = FALSE;

    private String name;

    private final String cellPhone;

    private static final String STREAM_ID = "personEventStream";

    private static Faker faker = new Faker();

    private Collection<Runnable> tickConsumers = ImmutableList.of();

    private Company company;
}
