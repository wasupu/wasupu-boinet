package io.wasupu.boinet.financial;

import io.wasupu.boinet.financial.eventPublisher.AccountEventPublisher;

import java.math.BigDecimal;

public class Account {

    public Account(String iban, AccountEventPublisher accountEventPublisher) {
        this.iban = iban;

        this.accountEventPublisher = accountEventPublisher;
    }

    public BigDecimal getBalance() {
        return amount;
    }

    public void withdraw(BigDecimal amount) {
        this.amount = this.amount.subtract(amount);

        accountEventPublisher.publishWithdrawal(iban, amount, getBalance());
    }

    public String getIban() {
        return iban;
    }

    public void deposit(BigDecimal amount) {
        this.amount = this.amount.add(amount);

        accountEventPublisher.publishDeposit(iban, amount, getBalance());
    }

    private BigDecimal amount = new BigDecimal(0);

    private String iban;

    private AccountEventPublisher accountEventPublisher;
}
