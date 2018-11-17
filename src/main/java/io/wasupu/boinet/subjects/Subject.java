package io.wasupu.boinet.subjects;

import com.google.common.collect.ImmutableList;
import io.wasupu.boinet.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Subject {

    public Subject(String identifier, World world){
        this.identifier = identifier;
        this.world = world;
    }

    public void removeBehaviour(Behaviour tickConsumer) {
        var ticksToRemove = new ArrayList<>(behaviours);
        ticksToRemove.removeIf(tickConsumerElement -> tickConsumerElement.getIdentifier().equals(tickConsumer.getIdentifier()));

        behaviours = ImmutableList
            .<Behaviour>builder()
            .addAll(ticksToRemove)
            .build();
    }

    public void addBehaviour(Behaviour tickConsumer) {
        behaviours = ImmutableList
            .<Behaviour>builder()
            .addAll(behaviours)
            .add(tickConsumer)
            .build();
    }

    public Boolean existsBehaviour(Behaviour economicalSubjectBehaviour) {
        return behaviours.contains(economicalSubjectBehaviour);
    }

    public void tick() {
        executeBehaviours();
    }

    public World getWorld() {
        return world;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        var subject = (Subject) o;

        return identifier.equals(subject.identifier);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    private void executeBehaviours() {
        behaviours.forEach(Behaviour::tick);
    }

    private Collection<Behaviour> behaviours = List.of();

    private World world;

    private String identifier;
}
