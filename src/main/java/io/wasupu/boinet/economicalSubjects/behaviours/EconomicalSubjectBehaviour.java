package io.wasupu.boinet.economicalSubjects.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.EconomicalSubject;
import io.wasupu.boinet.subjects.Behaviour;

import java.util.UUID;

public abstract class EconomicalSubjectBehaviour implements Behaviour {

    public EconomicalSubjectBehaviour(World world, EconomicalSubject economicalSubject) {
        this.world = world;
        this.economicalSubject = economicalSubject;
    }

    public String getIdentifier(){
        return identifier;
    }

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
