package io.wasupu.boinet.financial;

import io.wasupu.boinet.World;

import java.math.BigDecimal;
import java.util.Map;

public class Mortgage {

    public Mortgage(String mortgageIdentifier, String userIdentifier, BigDecimal originalAmount, String iban, World world) {
        this.mortgageIdentifier = mortgageIdentifier;
        this.originalAmount = originalAmount;
        this.iban = iban;
        this.world = world;
        this.userIdentifier = userIdentifier;
    }

    public String getIban() {
        return iban;
    }

    public BigDecimal getAmortizedAmount() {
        return amortizedAmount;
    }

    public BigDecimal getOriginalAmount(){
        return originalAmount;
    }

    public Boolean isAmortized() {
        return originalAmount.compareTo(amortizedAmount) == 0;
    }

    public String getUserIdentifier(){
        return userIdentifier;
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

    private String userIdentifier;
}
