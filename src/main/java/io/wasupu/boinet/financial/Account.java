package io.wasupu.boinet.financial;

import io.wasupu.boinet.World;

import java.math.BigDecimal;
import java.util.Map;

public class Account {

    public Account(String iban, World world) {
        this.iban = iban;
        this.world = world;
    }

    public BigDecimal getBalance() {
        return amount;
    }

    public void withdraw(BigDecimal amount) {
        this.amount = this.amount.subtract(amount);
    }

    public void deposit(BigDecimal amount) {
        this.amount = this.amount.add(amount);

        publishAccountDeposit(iban, amount, getBalance());
    }

    private void publishAccountDeposit(String iban, BigDecimal amount, BigDecimal balance) {
        world.getEvenPublisher().publish(
            Map.of("eventType", "deposit",
                "iban", iban,
                "amount", amount,
                "amount.currency", "EUR",
                "balance", balance,
                "balance.currency", "EUR",
                "date", world.getCurrentDateTime().toDate()));
    }

    private BigDecimal amount = new BigDecimal(0);

    private String iban;

    private World world;
}
