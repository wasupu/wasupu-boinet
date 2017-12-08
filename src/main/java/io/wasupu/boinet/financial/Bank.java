package io.wasupu.boinet.financial;

import com.google.common.collect.ImmutableMap;
import io.wasupu.boinet.World;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Bank {

    public Bank(World world) {
        this.world = world;
    }

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

    public void processPayment(BigDecimal amount, String pan, String sellerAccount, String companyIdentifier, String details, Pair<Double, Double> coordinates) {
        String buyerIban = cards.get(pan);
        Account fromAccount = accounts.get(buyerIban);
        if (fromAccount.getBalance().compareTo(amount) < 0) return;

        transfer(buyerIban, sellerAccount, amount);
        publishCardPayment(amount, pan, companyIdentifier, details, coordinates);
    }

    public BigDecimal getBalance(String iban) {
        return accounts.get(iban).getBalance();
    }

    public boolean existAccount(String iban) {
        return accounts.containsKey(iban);
    }

    public void transfer(String ibanFrom, String ibanTo, BigDecimal amount) {
        Account fromAccount = accounts.get(ibanFrom);
        if (fromAccount.getBalance().compareTo(amount) < 0) return;

        Account toAccount = accounts.get(ibanTo);

        fromAccount.withdraw(amount);
        toAccount.deposit(amount);
    }

    String getIbanByPan(String pan) {
        return cards.get(pan);
    }

    private void publishCardPayment(BigDecimal amount, String pan, String companyIndentifier, String details,
                                    Pair<Double, Double> coordinates) {
        world.getEventCardEventPublisher().publish(ImmutableMap
            .<String, Object>builder()
            .put("pan", pan)
            .put("amount", amount)
            .put("currency", "EUR")
            .put("details", details)
            .put("geolocation", ImmutableMap.of(
                "latitude", coordinates.getLeft().toString(),
                "longitude", coordinates.getRight().toString()))
            .put("company", companyIndentifier)
            .put("date", world.getCurrentDateTime().toDate())
            .build());
    }

    private Map<String, Account> accounts = new HashMap<>();

    private Map<String, String> cards = new HashMap<>();

    private int iban = 0;

    private int pan = 0;

    private World world;



}
