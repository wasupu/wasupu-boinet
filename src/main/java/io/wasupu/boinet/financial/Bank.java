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

    public String contractAccount(String userIdentifier) {
        var newIban = String.valueOf(iban);

        accounts.put(newIban, new Account(newIban));

        iban++;
        publishNewAccountEvent(userIdentifier, newIban);

        return newIban;
    }

    public String contractDebitCard(String iban) {
        var panAsString = String.valueOf(pan);
        cards.put(panAsString, String.valueOf(iban));
        pan++;

        return panAsString;
    }

    public void deposit(String iban, BigDecimal amount) {
        var account = accounts.get(iban);
        account.deposit(amount);
    }

    public void processPayment(BigDecimal amount, String pan, String sellerAccount, String companyIdentifier, String details, Pair<Double, Double> coordinates) {
        var buyerIban = cards.get(pan);
        var fromAccount = accounts.get(buyerIban);
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
        var fromAccount = accounts.get(ibanFrom);
        if (fromAccount.getBalance().compareTo(amount) < 0) return;

        var toAccount = accounts.get(ibanTo);

        fromAccount.withdraw(amount);
        toAccount.deposit(amount);
    }

    String getIbanByPan(String pan) {
        return cards.get(pan);
    }

    private void publishNewAccountEvent(String userIdentifier, String newIban) {
        world.getEvenPublisher().publish(Map.of("eventType", "newAccount",
            "iban", newIban,
            "user", userIdentifier));
    }

    private void publishCardPayment(BigDecimal amount, String pan, String companyIdentifier, String details, Pair<Double, Double> coordinates) {
        world.getEvenPublisher().publish(ImmutableMap
            .<String, Object>builder()
            .put("pan", pan)
            .put("amount", amount)
            .put("currency", "EUR")
            .put("details", details)
            .put("geolocation", Map.of(
                "latitude", coordinates.getLeft().toString(),
                "longitude", coordinates.getRight().toString()))
            .put("company", companyIdentifier)
            .put("date", world.getCurrentDateTime().toDate())
            .build());
    }

    private Map<String, Account> accounts = new HashMap<>();

    private Map<String, String> cards = new HashMap<>();

    private int iban = 0;

    private int pan = 0;

    private World world;
}
