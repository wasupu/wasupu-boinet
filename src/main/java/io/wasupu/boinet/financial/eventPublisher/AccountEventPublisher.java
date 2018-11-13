package io.wasupu.boinet.financial.eventPublisher;

import io.wasupu.boinet.World;

import java.math.BigDecimal;
import java.util.Map;

import static io.wasupu.boinet.financial.Money.convertMoneyToJson;

public class AccountEventPublisher {

    public AccountEventPublisher(World world) {
        this.world = world;
    }

    public void publishContractAccount(String userIdentifier, String newIban) {
        world.getEventPublisher().publish(Map.of(
            "eventType", "contractAccount",
            "iban", newIban,
            "user", userIdentifier,
            "date", world.getCurrentDateTime().toDate()));
    }

    public void publishDeposit(String iban, BigDecimal amount, BigDecimal balance) {
        world.getEventPublisher().publish(Map.of(
            "eventType", "deposit",
            "iban", iban,
            "amount", convertMoneyToJson(amount),
            "balance", convertMoneyToJson(balance),
            "date", world.getCurrentDateTime().toDate()));
    }

    public void publishWithdrawal(String iban, BigDecimal amount, BigDecimal balance) {
        world.getEventPublisher().publish(Map.of(
            "eventType", "withdrawal",
            "iban", iban,
            "amount", convertMoneyToJson(amount),
            "balance", convertMoneyToJson(balance),
            "date", world.getCurrentDateTime().toDate()));
    }

    private final World world;
}
