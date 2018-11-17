package io.wasupu.boinet.economicalSubjects.behaviours.recurrent;

import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.EconomicalSubject;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;
import io.wasupu.boinet.subjects.Behaviour;

public class EveryDay extends EconomicalSubjectBehaviour {

    public EveryDay(World world,
                    EconomicalSubject economicalSubject,
                    Behaviour behaviour) {
        super(world, economicalSubject);
        this.economicalSubject = economicalSubject;
        this.behaviour = behaviour;
    }

    public void tick() {
        if (economicalSubject.getAge() < 2) return;

        behaviour.tick();
    }

    @Override
    public String getIdentifier() {
        return behaviour.getIdentifier();
    }

    private EconomicalSubject economicalSubject;

    private Behaviour behaviour;
}
