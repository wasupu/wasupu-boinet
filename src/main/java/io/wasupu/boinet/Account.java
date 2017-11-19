package io.wasupu.boinet;

import java.math.BigDecimal;

public class Account {

    public Account(String iban) {
        this.iban = iban;
    }

    public BigDecimal getBalance() {
        return amount;
    }

    public void withdraw(BigDecimal amount) {
        this.amount = this.amount.subtract(amount);
    }

    public void deposit(BigDecimal amount) {
        this.amount = this.amount.add(amount);
    }

    private BigDecimal amount = new BigDecimal(0);

    private String iban;

}
