package io.wasupu.boinet;

import com.github.javafaker.Faker;
import com.google.common.collect.ImmutableMap;

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

    public void processPayment(BigDecimal amount, String pan, String sellerAccount, String companyIdentifier, String details) {
        String buyerAccount = cards.get(pan);

        transfer(buyerAccount, sellerAccount, amount);
        publishMovement(amount, pan, companyIdentifier, details);
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

    private void publishMovement(BigDecimal amount, String pan, String companyIndentifier, String details) {
        world.getEventPublisher().publish(STREAM_ID, ImmutableMap
            .<String, Object>builder()
            .put("pan", pan)
            .put("amount", amount)
            .put("currency", "EUR")
            .put("details", details)
            .put("geolocation", ImmutableMap.of(
                "latitude", faker.address().latitude(),
                "longitude", faker.address().longitude()))
            .put("company", companyIndentifier)
            .put("date", world.getCurrentDateTime().toDate())
            .build());
    }

    private Map<String, Account> accounts = new HashMap<>();

    private Map<String, String> cards = new HashMap<>();

    private int iban = 0;

    private int pan = 0;

    private World world;

    private static final String STREAM_ID = "cardMovementEventStream";

    private static final Faker faker = new Faker();

}
