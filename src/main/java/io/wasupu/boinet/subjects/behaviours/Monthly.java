package io.wasupu.boinet.subjects.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.EconomicalSubject;
import io.wasupu.boinet.subjects.Behaviour;
import io.wasupu.boinet.subjects.Subject;

public class Monthly implements Behaviour {

    public Monthly(World world,
                   Integer day,
                   Behaviour behaviour) {
        this.world = world;
        this.day = day;
        this.behaviour = behaviour;
    }

    public void tick() {
        if (!isDayOfMonth(day)) return;

        behaviour.tick();
    }

    @Override
    public String getIdentifier() {
        return behaviour.getIdentifier();
    }

    private boolean isDayOfMonth(Integer dayOfMonth) {
        return dayOfMonth.equals(world.getCurrentDateTime().getDayOfMonth());
    }

    private Integer day;

    private Behaviour behaviour;

    private World world;
}
