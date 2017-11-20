package io.wasupu.boinet;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static net.logstash.logback.marker.Markers.appendEntries;

public class Bank {

    public Bank(World world){
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

    private void publishMovement(BigDecimal amount, String pan, String companyIndentifier) {
        logger.info(appendEntries(ImmutableMap
                .builder()
                .put("pan", pan)
                .put("amount", amount)
                .put("currency", "EUR")
                .put("company",companyIndentifier)
                .put("date", world.getCurrentDate())
                .build()),
            "Movement");
    }

    private Map<String, Account> accounts = new HashMap<>();

    private Map<String, String> cards = new HashMap<>();

    private int iban = 0;

    private int pan = 0;

    private World world;

    private static Logger logger = LoggerFactory.getLogger(Bank.class);
}
