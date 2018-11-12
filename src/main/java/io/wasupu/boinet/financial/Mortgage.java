package io.wasupu.boinet.financial;

import io.wasupu.boinet.World;

import java.math.BigDecimal;
import java.util.Map;

import static io.wasupu.boinet.financial.Money.convertMoneyToJson;

public class Mortgage {

    public Mortgage(String mortgageIdentifier, String userIdentifier, BigDecimal totalAmount, String iban, World world) {
        this.mortgageIdentifier = mortgageIdentifier;
        this.totalAmount = totalAmount;
        this.iban = iban;
        this.world = world;
        this.userIdentifier = userIdentifier;
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

        world.getEventPublisher().publish(Map.of(
            "eventType", "payMortgageInstallment",
            "mortgageIdentifier", mortgageIdentifier,
            "iban", iban,
            "totalAmount", convertMoneyToJson(totalAmount),
            "installmentAmount", convertMoneyToJson(amount),
            "totalAmortizedAmount", convertMoneyToJson(amortizedAmount),
            "date", world.getCurrentDateTime().toDate()));
    }

    private BigDecimal totalAmount;

    private BigDecimal amortizedAmount = new BigDecimal(0);

    private String mortgageIdentifier;

    private String iban;

    private World world;

    private String userIdentifier;
}
