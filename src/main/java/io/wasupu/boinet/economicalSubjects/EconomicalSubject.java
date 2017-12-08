package io.wasupu.boinet.economicalSubjects;

import com.github.javafaker.Faker;
import com.google.common.collect.ImmutableList;
import io.wasupu.boinet.World;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;

public abstract class EconomicalSubject {

    public EconomicalSubject(String identifier, World world) {
        this.identifier = identifier;
        this.world = world;

        coordinates = this.getWorld().getGPS().coordinates();
        this.latitude = coordinates.getLeft().toString();
        this.longitude = coordinates.getRight().toString();

        world.listenTicks(this::tick);
    }

    public void listenTicks(Runnable tickConsumer) {
        tickConsumers = ImmutableList
            .<Runnable>builder()
            .addAll(tickConsumers)
            .add(tickConsumer)
            .build();
    }

    public void tick() {
        executeBehaviours();
        publishBalance();

        increaseAge();
    }

    private void executeBehaviours() {
        tickConsumers.forEach(Runnable::run);
    }

    protected abstract void publishBalance();

    public static void setFaker(Faker newFaker) {
        faker = newFaker;
    }

    public String getIban() {
        return iban;
    }

    public Long getAge() {
        return age;
    }

    public void increaseAge() {
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

    private Collection<Runnable> tickConsumers = ImmutableList.of();

}
