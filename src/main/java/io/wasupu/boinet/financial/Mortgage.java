package io.wasupu.boinet.financial;

import java.math.BigDecimal;

public class Mortgage {

    public Mortgage(String mortgageIdentifier, BigDecimal originalAmount, String iban) {
        this.mortgageIdentifier = mortgageIdentifier;
        this.originalAmount = originalAmount;
        this.iban = iban;
    }

    private BigDecimal originalAmount = new BigDecimal(0);

    private BigDecimal amortizedAmount = new BigDecimal(0);

    private String mortgageIdentifier;

    private String iban;

}
