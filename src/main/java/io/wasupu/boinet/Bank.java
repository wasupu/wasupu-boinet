package io.wasupu.boinet;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Bank {

    public String contractAccount() {
        String newIban = String.valueOf(iban);
        accounts.put(newIban, new Account(newIban));
        iban++;

        return newIban;
    }

    public String contractDebitCard(String iban) {
        String panAsString = String.valueOf(pan);
        cards.put(panAsString, String.valueOf(iban));
        pan++;

        return panAsString;
    }

    public void deposit(String iban, BigDecimal amount) {
        Account account = accounts.get(iban);
        account.deposit(amount);
    }

    public void processPayment(BigDecimal amount, String pan, String sellerAccount, String companyIdentifier) {
        String buyerAccount = cards.get(pan);

        transfer(buyerAccount, sellerAccount, amount);
        publishMovement(amount, pan, companyIdentifier);
    }

    public BigDecimal getBalance(String iban) {
        return accounts.get(iban).getBalance();
    }

    public boolean existAccount(String iban) {
        return accounts.containsKey(iban);
    }

    public void transfer(String ibanFrom, String ibanTo, BigDecimal amount) {
        Account fromAccount = accounts.get(ibanFrom);
        Account toAccount = accounts.get(ibanTo);

        fromAccount.withdraw(amount);
        toAccount.deposit(amount);
    }

    String getIbanByPan(String pan) {
        return cards.get(pan);
    }

    private void publishMovement(BigDecimal amount, String pan, String fuc) {
        System.out.println("pan:" + pan + "->" + "amount:" + amount + "->" + "fuc:" + fuc);
    }

    private Map<String, Account> accounts = new HashMap<>();

    private Map<String, String> cards = new HashMap<>();

    private int iban = 0;

    private int pan = 0;


}
