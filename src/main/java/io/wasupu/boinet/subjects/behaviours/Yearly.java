package io.wasupu.boinet.subjects.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.subjects.Behaviour;

public class Yearly implements Behaviour {

    public Yearly(World world,
                  Integer day,
                  Behaviour behaviour) {
        this.day = day;
        this.behaviour = behaviour;
        this.world = world;
    }

    @Override
    public String getIdentifier() {
        return behaviour.getIdentifier();
    }

    public void tick() {
        if (!isDayOfTheYear()) return;

        behaviour.tick();
    }

    private boolean isDayOfTheYear() {
        return world.getCurrentDateTime().getDayOfYear() == day;
    }

    private int day;

    private Behaviour behaviour;

    private World world;
}
