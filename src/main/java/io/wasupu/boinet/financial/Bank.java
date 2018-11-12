package io.wasupu.boinet.financial;

import io.wasupu.boinet.World;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static io.wasupu.boinet.financial.Money.*;

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

    public void deposit(String iban, BigDecimal amount) {
        var account = accounts.get(iban);
        account.deposit(amount);
    }

    public void transfer(String ibanFrom, String ibanTo, BigDecimal amount) {
        var fromAccount = accounts.get(ibanFrom);
        if (fromAccount.getBalance().compareTo(amount) < 0) return;

        var toAccount = accounts.get(ibanTo);

        fromAccount.withdraw(amount);
        toAccount.deposit(amount);
    }

    public BigDecimal getBalance(String iban) {
        return accounts.get(iban).getBalance();
    }

    public boolean existAccount(String iban) {
        return accounts.containsKey(iban);
    }

    public String contractDebitCard(String userIdentifier, String iban) {
        var panAsString = String.valueOf(pan);
        cards.put(panAsString, String.valueOf(iban));
        pan++;

        publishContractDebitCard(userIdentifier, iban, panAsString);

        return panAsString;
    }

    public void processCardPayment(BigDecimal amount, String pan, String sellerAccount, String companyIdentifier, String details, Pair<Double, Double> coordinates) {
        var buyerIban = cards.get(pan);
        var fromAccount = accounts.get(buyerIban);

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            publishDeclineCardPayment(amount, pan, companyIdentifier, details, coordinates);
            return;
        }

        transfer(buyerIban, sellerAccount, amount);
        publishCardPayment(amount, pan, companyIdentifier, details, coordinates);
    }

    public String contractMortgage(String userIdentifier, String iban, BigDecimal amount) {
        var mortgageIdentifierAsString = String.valueOf(mortgageIdentifier);

        mortgages.put(mortgageIdentifierAsString, new Mortgage(mortgageIdentifierAsString, userIdentifier, amount, iban, world));
        mortgageIdentifier++;

        publishContractMortgage(userIdentifier, iban, mortgageIdentifierAsString, amount);

        return mortgageIdentifierAsString;
    }

    public void payMortgage(String mortgageIdentifier, BigDecimal amount) {
        var mortgage = mortgages.get(mortgageIdentifier);

        var pendingAmount = mortgage.getTotalAmount().subtract(mortgage.getAmortizedAmount());
        var account = accounts.get(mortgage.getIban());

        var installmentAmount = pendingAmount.compareTo(amount) < 0 ? pendingAmount : amount;

        if (account.getBalance().compareTo(installmentAmount) < 0) {
            publishDeclineMortgageInstallment(mortgage, installmentAmount);

            return;
        }

        account.withdraw(installmentAmount);
        mortgage.amortize(installmentAmount);
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

    String getIbanByPan(String pan) {
        return cards.get(pan);
    }

    private void publishContractAccountEvent(String userIdentifier, String newIban) {
        world.getEventPublisher().publish(Map.of(
            "eventType", "contractAccount",
            "iban", newIban,
            "user", userIdentifier,
            "date", world.getCurrentDateTime().toDate()));
    }

    private void publishContractDebitCard(String identifier, String iban, String panAsString) {
        world.getEventPublisher().publish(Map.of(
            "eventType", "contractDebitCard",
            "iban", iban,
            "pan", panAsString,
            "user", identifier,
            "date", world.getCurrentDateTime().toDate()));
    }

    private void publishContractMortgage(String identifier, String iban, String mortgageIdentifier, BigDecimal amount) {
        world.getEventPublisher().publish(Map.of(
            "eventType", "contractMortgage",
            "iban", iban,
            "mortgageAmount", convertMoneyToJson(amount),
            "mortgageIdentifier", mortgageIdentifier,
            "user", identifier,
            "date", world.getCurrentDateTime().toDate()));
    }

    private void publishCardPayment(BigDecimal amount, String pan, String companyIdentifier, String details, Pair<Double, Double> coordinates) {
        world.getEventPublisher().publish(Map.of(
            "eventType", "acceptPayment",
            "pan", pan,
            "amount", Map.of(
                "value", amount,
                "currency", "EUR"),
            "details", details,
            "geolocation", Map.of(
                "latitude", coordinates.getLeft(),
                "longitude", coordinates.getRight()),
            "company", companyIdentifier,
            "date", world.getCurrentDateTime().toDate()));
    }

    private void publishDeclineCardPayment(BigDecimal amount, String pan, String companyIdentifier, String details, Pair<Double, Double> coordinates) {
        world.getEventPublisher().publish(Map.of(
            "eventType", "declinePayment",
            "pan", pan,
            "amount", Map.of(
                "value", amount,
                "currency", "EUR"),
            "details", details,
            "geolocation", Map.of(
                "latitude", coordinates.getLeft(),
                "longitude", coordinates.getRight()),
            "company", companyIdentifier,
            "date", world.getCurrentDateTime().toDate()));
    }

    private void publishCancelMortgage(String userIdentifier, String iban, String mortgageIdentifier) {
        world.getEventPublisher().publish(Map.of(
            "eventType", "cancelMortgage",
            "iban", iban,
            "mortgageIdentifier", mortgageIdentifier,
            "user", userIdentifier,
            "date", world.getCurrentDateTime().toDate()));
    }

    private void publishDeclineMortgageInstallment(Mortgage mortgage, BigDecimal installmentAmount) {
        world.getEventPublisher().publish(Map.of(
            "eventType", "declineMortgageInstallment",
            "mortgageIdentifier", mortgage.getIdentifier(),
            "iban", mortgage.getIban(),
            "totalAmount", convertMoneyToJson(mortgage.getTotalAmount()),
            "installmentAmount", convertMoneyToJson(installmentAmount),
            "totalAmortizedAmount", convertMoneyToJson(mortgage.getAmortizedAmount()),
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
