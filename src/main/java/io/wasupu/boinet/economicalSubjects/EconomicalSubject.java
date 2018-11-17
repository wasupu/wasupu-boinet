package io.wasupu.boinet.economicalSubjects;

import com.github.javafaker.Faker;
import io.wasupu.boinet.World;
import io.wasupu.boinet.subjects.Subject;
import org.apache.commons.lang3.tuple.Pair;

public abstract class EconomicalSubject extends Subject {

    public EconomicalSubject(String identifier, World world) {
        super(identifier, world);

        var address = faker.address();
        this.fullAddress = address.fullAddress();
        this.zipCode = address.zipCode();

        coordinates = this.getWorld().getGPS().coordinates();

        world.listenTicks(this::tick);
    }

    public void tick() {
        super.tick();
        increaseAge();
    }

    public abstract EconomicalSubjectType getType();

    public abstract String getName();

    public static void setFaker(Faker newFaker) {
        faker = newFaker;
    }

    public String getIban() {
        return iban;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public String getZipCode() {
        return zipCode;
    }

    public Long getAge() {
        return age;
    }

    public void increaseAge() {
        age++;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public Pair<Double, Double> getCoordinates() {
        return coordinates;
    }

    private String iban;

    private Long age = 0L;

    protected static Faker faker = new Faker();

    private String fullAddress;

    private String zipCode;

    private final Pair<Double, Double> coordinates;
}
