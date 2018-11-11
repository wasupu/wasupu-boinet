package io.wasupu.boinet.economicalSubjects.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.EconomicalSubject;

import java.util.UUID;

public abstract class EconomicalSubjectBehaviour {

    public EconomicalSubjectBehaviour(World world, EconomicalSubject economicalSubject) {
        this.world = world;
        this.economicalSubject = economicalSubject;
    }

    public String getIdentifier(){
        return identifier;
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

    private String identifier = UUID.randomUUID().toString();
}
