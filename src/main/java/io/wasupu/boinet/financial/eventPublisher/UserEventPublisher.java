package io.wasupu.boinet.financial.eventPublisher;

import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.EconomicalSubject;

import java.util.Map;

public class UserEventPublisher {

    public UserEventPublisher(World world) {
        this.world = world;
    }


    public void publishRegisterUserEvent(EconomicalSubject subject) {
        world.getEventPublisher().publish(Map.of(
            "eventType", "registerUser",
            "type", subject.getType().toString(),
            "user", subject.getIdentifier(),
            "name", subject.getName(),
            "fullAddress", subject.getFullAddress(),
            "zip", subject.getZipCode(),
            "date", world.getCurrentDateTime().toDate()));
    }

    private World world;
}
