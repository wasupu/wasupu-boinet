package io.wasupu.boinet.subjects.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.subjects.Behaviour;

public class Weekly implements Behaviour {

    public Weekly(World world,
                  Integer day,
                  Behaviour behaviour) {
        this.world = world;
        this.day = day;
        this.behaviour = behaviour;
    }

    public void tick() {
        if (!isDayInTheWeek()) return;

        behaviour.tick();
    }

    @Override
    public String getIdentifier() {
        return behaviour.getIdentifier();
    }

    private boolean isDayInTheWeek() {
        return world.getCurrentDateTime().getDayOfWeek() == day;
    }

    private int day;

    private Behaviour behaviour;

    private World world;
}
