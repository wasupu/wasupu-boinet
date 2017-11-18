package io.wasupu.boinet;

import java.math.BigDecimal;

public class Account {

    public Account(String iban) {
        this.iban = iban;
    }

    public BigDecimal getBalance() {
        return amount;
    }

    public void withdraw(BigDecimal salary) {
        amount = amount.subtract(salary);
    }

    public void deposit(BigDecimal salary) {
        amount = amount.add(salary);
    }

    private BigDecimal amount = new BigDecimal(0);

    private String iban;
}
