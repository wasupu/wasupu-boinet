package io.wasupu.boinet.population;

import com.github.javafaker.Address;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.wasupu.boinet.Company;
import io.wasupu.boinet.EconomicalSubject;
import io.wasupu.boinet.World;

import java.util.Collection;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class Person extends EconomicalSubject{

    public Person(String identifier,
                  World world) {
        super(identifier,world);

        this.name = faker.name().fullName();
        this.cellPhone = faker.phoneNumber().cellPhone();
        Address address = faker.address();
        this.fullAddress = address.fullAddress();
        this.zipCode = address.zipCode();
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

        increaseAge();
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

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public Company getEmployer() {
        return company;
    }

    private void publishPersonBalance() {
        if (getAge() % 30 != 0) return;

        getWorld().getEventPersonPublisher().publish(ImmutableMap
            .<String, Object>builder()
            .put("person", getIdentifier())
            .put("name", name)
            .put("cellPhone", cellPhone)
            .put("pan", pan)
            .put("address", ImmutableMap.of(
                "full", fullAddress,
                "zipCode", zipCode,
                "geolocation", ImmutableMap.of(
                    "latitude", getLatitude(),
                    "longitude", getLongitude())))
            .put("balance", getWorld().getBank().getBalance(getIban()))
            .put("currency", "EUR")
            .put("date", getWorld().getCurrentDateTime().toDate())
            .put("eventType", "personBalance")
            .build());
    }

    private String fullAddress;

    private String zipCode;

    private String pan;

    private Boolean employed = FALSE;

    private String name;

    private final String cellPhone;

    private Collection<Runnable> tickConsumers = ImmutableList.of();

    private Company company;
}
