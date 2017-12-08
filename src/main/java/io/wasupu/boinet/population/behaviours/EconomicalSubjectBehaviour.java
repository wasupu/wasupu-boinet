package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.EconomicalSubject;
import io.wasupu.boinet.World;
import io.wasupu.boinet.population.Person;

public abstract class EconomicalSubjectBehaviour {

    public EconomicalSubjectBehaviour(World world, EconomicalSubject economicalSubject) {
        this.world = world;
        this.economicalSubject = economicalSubject;
    }

    public abstract void tick();

    public World getWorld() {
        return world;
    }

    public EconomicalSubject getEconomicalSubject() {
        return economicalSubject;
    }

    private World world;

    private EconomicalSubject economicalSubject;
}
