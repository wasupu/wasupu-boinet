package io.wasupu.boinet.financial;

import io.wasupu.boinet.World;

import java.math.BigDecimal;
import java.util.Map;

public class Mortgage {

    public Mortgage(String mortgageIdentifier, BigDecimal originalAmount, String iban, World world) {
        this.mortgageIdentifier = mortgageIdentifier;
        this.originalAmount = originalAmount;
        this.iban = iban;
        this.world = world;
    }

    public String getIban() {
        return iban;
    }

    public BigDecimal getAmortizedAmount() {
        return amortizedAmount;
    }

    public void amortize(BigDecimal amount) {
        amortizedAmount = amortizedAmount.add(amount);

        world.getEvenPublisher().publish(Map.of(
            "eventType", "mortgageAmortization",
            "mortgageIdentifier", mortgageIdentifier,
            "iban", iban,
            "originalAmount", originalAmount,
            "originalAmount.currency", "EUR",
            "amortizedAmount", amount,
            "amortizedAmount.currency", "EUR",
            "totalAmortizedAmount", amortizedAmount,
            "totalAmortizedAmount.currency", "EUR",
            "date", world.getCurrentDateTime().toDate()));
    }

    private BigDecimal originalAmount;

    private BigDecimal amortizedAmount = new BigDecimal(0);

    private String mortgageIdentifier;

    private String iban;

    private World world;
}
