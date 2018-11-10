package io.wasupu.boinet.financial;

import io.wasupu.boinet.World;

import java.math.BigDecimal;

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
    }

    private BigDecimal originalAmount = new BigDecimal(0);

    private BigDecimal amortizedAmount = new BigDecimal(0);

    private String mortgageIdentifier;

    private String iban;

    private World world;
}
