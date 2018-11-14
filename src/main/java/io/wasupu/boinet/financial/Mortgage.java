package io.wasupu.boinet.financial;

import io.wasupu.boinet.World;
import io.wasupu.boinet.financial.eventPublisher.MortgageEventPublisher;

import java.math.BigDecimal;

public class Mortgage {

    public Mortgage(String mortgageIdentifier, String userIdentifier, BigDecimal totalAmount, String iban, World world) {
        this.mortgageIdentifier = mortgageIdentifier;
        this.totalAmount = totalAmount;
        this.iban = iban;
        this.userIdentifier = userIdentifier;
        this.mortgageEventPublisher = new MortgageEventPublisher(world);

    }

    public String getIban() {
        return iban;
    }

    public String getIdentifier() {
        return mortgageIdentifier;
    }

    public BigDecimal getAmortizedAmount() {
        return amortizedAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public Boolean isAmortized() {
        return totalAmount.compareTo(amortizedAmount) == 0;
    }

    public String getUserIdentifier() {
        return userIdentifier;
    }

    public void amortize(BigDecimal amount) {
        amortizedAmount = amortizedAmount.add(amount);

        mortgageEventPublisher.publisPayMortgageInstallment(amount, this);
    }

    private BigDecimal totalAmount;

    private BigDecimal amortizedAmount = new BigDecimal(0);

    private String mortgageIdentifier;

    private String iban;

    private String userIdentifier;

    private MortgageEventPublisher mortgageEventPublisher;
}
