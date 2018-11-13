package io.wasupu.boinet.financial;

import io.wasupu.boinet.World;
import io.wasupu.boinet.companies.Company;
import io.wasupu.boinet.companies.ReceiptType;
import io.wasupu.boinet.economicalSubjects.EconomicalSubject;
import io.wasupu.boinet.financial.eventPublisher.*;
import io.wasupu.boinet.population.Person;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Bank {

    public Bank(World world) {
        this.world = world;

        this.bankEventPublisher = new ReceiptEventPublisher(world);
        this.accountEventPublisher = new AccountEventPublisher(world);
        this.mortgageEventPublisher = new MortgageEventPublisher(world);
        this.debitCardEventPublisher = new DebitCardEventPublisher(world);
        this.userEventPublisher = new UserEventPublisher(world);
    }

    public String contractAccount(String userIdentifier) {
        var newIban = String.valueOf(iban);

        accounts.put(newIban, new Account(newIban, accountEventPublisher));
        iban++;

        accountEventPublisher.publishContractAccount(userIdentifier, newIban);

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

        fromAccount.withdrawal(amount);
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

        debitCardEventPublisher.publishContractDebitCard(userIdentifier, iban, panAsString);

        return panAsString;
    }

    public void payWithCard(BigDecimal amount, String pan, String sellerIban, String companyIdentifier, String details, Pair<Double, Double> coordinates) {
        var fromAccount = accounts.get(cards.get(pan));

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            debitCardEventPublisher.publishDeclineCardPayment(amount, pan, companyIdentifier, details, coordinates);
            return;
        }

        transfer(cards.get(pan), sellerIban, amount);
        debitCardEventPublisher.publishCardPayment(amount, pan, companyIdentifier, details, coordinates);
    }

    public void payReceipt(String receiptId, ReceiptType receiptType, BigDecimal receiptAmount, Person person, Company company) {
        var fromAccount = accounts.get(person.getIban());

        if (fromAccount.getBalance().compareTo(receiptAmount) < 0) {
            return;
        }

        transfer(person.getIban(), company.getIban(),
            receiptAmount);

        bankEventPublisher.publishReceiptPayment(receiptId, receiptAmount, company.getIdentifier(), receiptType);
    }

    public String contractMortgage(String userIdentifier, String iban, BigDecimal amount) {
        var mortgageIdentifierAsString = String.valueOf(mortgageIdentifier);

        mortgages.put(mortgageIdentifierAsString, new Mortgage(mortgageIdentifierAsString, userIdentifier, amount, iban, world));
        mortgageIdentifier++;

        mortgageEventPublisher.publishContractMortgage(userIdentifier, iban, mortgageIdentifierAsString, amount);

        return mortgageIdentifierAsString;
    }

    public void payMortgage(String mortgageIdentifier, BigDecimal amount) {
        var mortgage = mortgages.get(mortgageIdentifier);

        var pendingAmount = mortgage.getTotalAmount().subtract(mortgage.getAmortizedAmount());
        var account = accounts.get(mortgage.getIban());

        var installmentAmount = pendingAmount.compareTo(amount) < 0 ? pendingAmount : amount;

        if (account.getBalance().compareTo(installmentAmount) < 0) {
            mortgageEventPublisher.publishDeclineMortgageInstallment(mortgage, installmentAmount);
            return;
        }

        account.withdrawal(installmentAmount);
        mortgage.amortize(installmentAmount);
    }

    public Boolean isMortgageAmortized(String mortgageIdentifier) {
        return mortgages.get(mortgageIdentifier).isAmortized();
    }

    public void cancelMortgage(String mortgageIdentifier) {
        var mortgage = mortgages.remove(mortgageIdentifier);

        mortgageEventPublisher.publishCancelMortgage(mortgage.getUserIdentifier(), mortgage.getIban(), mortgageIdentifier);
    }

    public void registerUser(EconomicalSubject subject) {
        userEventPublisher.publishRegisterUserEvent(subject);
    }

    public Boolean existMortgage(String mortgageIdentifier) {
        return mortgages.containsKey(mortgageIdentifier);
    }

    String getIbanByPan(String pan) {
        return cards.get(pan);
    }

    private Map<String, Account> accounts = new HashMap<>();

    private Map<String, String> cards = new HashMap<>();

    private Map<String, Mortgage> mortgages = new HashMap<>();

    private int iban = 0;

    private int pan = 0;

    private int mortgageIdentifier = 0;

    private World world;

    private ReceiptEventPublisher bankEventPublisher;

    private AccountEventPublisher accountEventPublisher;

    private MortgageEventPublisher mortgageEventPublisher;

    private DebitCardEventPublisher debitCardEventPublisher;

    private UserEventPublisher userEventPublisher;
}