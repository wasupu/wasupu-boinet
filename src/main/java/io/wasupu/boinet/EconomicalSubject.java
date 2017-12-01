package io.wasupu.boinet;

import com.github.javafaker.Faker;
import org.apache.commons.lang3.tuple.Pair;

public abstract class EconomicalSubject {

    public EconomicalSubject(String identifier, World world) {
        this.identifier = identifier;
        this.world = world;

        coordinates = this.getWorld().getGPS().coordinates();
        this.latitude = coordinates.getLeft().toString();
        this.longitude = coordinates.getRight().toString();

        world.listenTicks(this::tick);
    }

    public static void setFaker(Faker newFaker) {
        faker = newFaker;
    }

    public abstract void tick();

    public String getIban() {
        return iban;
    }

    public Long getAge() {
        return age;
    }

    public void increaseAge(){
        age++;
    }

    public String getIdentifier() {
        return identifier;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EconomicalSubject economicalSubject = (EconomicalSubject) o;

        return identifier.equals(economicalSubject.identifier);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public Pair<Double, Double> getCoordinates() {
        return coordinates;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    private String iban;

    private String identifier;

    private Long age = 0L;

    protected static Faker faker = new Faker();

    private World world;

    private String latitude;

    private String longitude;

    private final Pair<Double, Double> coordinates;
}
