package io.wasupu.boinet.financial.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.financial.Bank;
import io.wasupu.boinet.subjects.Behaviour;

import java.util.Map;
import java.util.UUID;

public class BankEconomicStatus implements Behaviour {

    public BankEconomicStatus(World world, Bank bank) {
        this.world = world;
        this.bank = bank;
    }

    @Override
    public void tick() {

        var treasuryAccountBalance = bank.getTreasuryAccount().getBalance();

        world.getEventPublisher().publish(Map.of(
            "eventType", "bankBalance",
            "treasuryAccount", treasuryAccountBalance));
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    private World world;

    private String identifier = UUID.randomUUID().toString();

    private Bank bank;
}
