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

        accounts.put(newIban, new Account(newIban, world));
        iban++;

        publishContractAccountEvent(userIdentifier, newIban);

        return newIban;
    }

    public String contractDebitCard(String userIdentifier, String iban) {
        var panAsString = String.valueOf(pan);
        cards.put(panAsString, String.valueOf(iban));
        pan++;

        publishContractDebitCard(userIdentifier, iban, panAsString);

        return panAsString;
    }

    public void deposit(String iban, BigDecimal amount) {
        var account = accounts.get(iban);
        account.deposit(amount);
    }

    public String contractMortgage(String userIdentifier, String iban, BigDecimal amount) {
        var mortgageIdentifierAsString = String.valueOf(mortgageIdentifier);

        mortgages.put(mortgageIdentifierAsString, new Mortgage(mortgageIdentifierAsString, userIdentifier, amount, iban, world));
        mortgageIdentifier++;

        publishContractMortgage(userIdentifier, iban, mortgageIdentifierAsString, amount);

        return mortgageIdentifierAsString;
    }

    public void payMortgage(String mortgageIdentifier, BigDecimal amortization) {
        var mortgage = mortgages.get(mortgageIdentifier);

        var mortgageAmortization = amortization;

        var pendingAmount = mortgage.getOriginalAmount().subtract(mortgage.getAmortizedAmount());

        if (pendingAmount.compareTo(amortization) < 1) {
            mortgageAmortization = pendingAmount;
        }

        var account = accounts.get(mortgage.getIban());

        account.withdraw(mortgageAmortization);
        mortgage.amortize(mortgageAmortization);
    }

    public Boolean isMortgageAmortized(String mortgageIdentifier) {
        return mortgages.get(mortgageIdentifier).isAmortized();
    }

    public void cancelMortgage(String mortgageIdentifier) {
        var mortgage = mortgages.remove(mortgageIdentifier);

        publishCancelMortgage(mortgage.getUserIdentifier(), mortgage.getIban(), mortgageIdentifier);
    }

    public Boolean existMortgage(String mortgageIdentifier) {
        return mortgages.containsKey(mortgageIdentifier);
    }

    public void processCardPayment(BigDecimal amount, String pan, String sellerAccount, String companyIdentifier, String details, Pair<Double, Double> coordinates) {
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

    private void publishContractAccountEvent(String userIdentifier, String newIban) {
        world.getEvenPublisher().publish(Map.of("eventType", "newAccount",
            "iban", newIban,
            "user", userIdentifier,
            "date", world.getCurrentDateTime().toDate()));
    }

    private void publishContractDebitCard(String identifier, String iban, String panAsString) {
        world.getEvenPublisher().publish(Map.of(
            "eventType", "newDebitCard",
            "iban", iban,
            "pan", panAsString,
            "user", identifier,
            "date", world.getCurrentDateTime().toDate()));
    }

    private void publishContractMortgage(String identifier, String iban, String mortgageIdentifier, BigDecimal amount) {
        world.getEvenPublisher().publish(Map.of(
            "eventType", "newMortgage",
            "iban", iban,
            "mortgageAmount", amount,
            "mortgageIdentifier", mortgageIdentifier,
            "user", identifier,
            "date", world.getCurrentDateTime().toDate()));
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

    private void publishCancelMortgage(String userIdentifier, String iban, String mortgageIdentifier) {
        world.getEvenPublisher().publish(Map.of(
            "eventType", "cancelMortgage",
            "iban", iban,
            "mortgageIdentifier", mortgageIdentifier,
            "user", userIdentifier,
            "date", world.getCurrentDateTime().toDate()));
    }

    private Map<String, Account> accounts = new HashMap<>();

    private Map<String, String> cards = new HashMap<>();

    private Map<String, Mortgage> mortgages = new HashMap<>();

    private int iban = 0;

    private int pan = 0;

    private int mortgageIdentifier = 0;

    private World world;
}
