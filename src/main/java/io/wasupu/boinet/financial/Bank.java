package io.wasupu.boinet.financial;

import io.wasupu.boinet.World;
import io.wasupu.boinet.companies.Company;
import io.wasupu.boinet.companies.ReceiptType;
import io.wasupu.boinet.economicalSubjects.EconomicalSubject;
import io.wasupu.boinet.financial.eventPublisher.*;
import io.wasupu.boinet.population.Person;
import io.wasupu.boinet.subjects.Subject;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Bank extends Subject {

    public Bank(World world, BigDecimal seedCapital) {
        super("Bank", world);

        this.receiptEventPublisher = new ReceiptEventPublisher(world);
        this.accountEventPublisher = new AccountEventPublisher(world);
        this.mortgageEventPublisher = new MortgageEventPublisher(world);
        this.debitCardEventPublisher = new DebitCardEventPublisher(world);
        this.userEventPublisher = new UserEventPublisher(world);

        this.treasuryAccount = new Account("bankTreasuryAccount", accountEventPublisher);
        this.treasuryAccount.deposit(seedCapital);
    }

    public String contractCurrentAccount(String userIdentifier) {
        var newIban = getNewIban();

        accounts.put(newIban, new Account(newIban, accountEventPublisher));

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

        fromAccount.withdraw(amount);
        toAccount.deposit(amount);
    }

    public BigDecimal getBalance(String iban) {
        return accounts.get(iban).getBalance();
    }

    public boolean existAccount(String iban) {
        return accounts.containsKey(iban);
    }

    private String getNewIban() {
        var id = "IBAN-" + ibanCounter;
        ibanCounter++;
        return id;
    }

    public String contractDebitCard(String userIdentifier, String iban) {
        var pan = getNewPan();
        cards.put(pan, String.valueOf(iban));

        debitCardEventPublisher.publishContractDebitCard(userIdentifier, iban, pan);

        return pan;
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

    private String getNewPan() {
        var id = "PAN-" + panCounter;
        panCounter++;
        return id;
    }

    public void payReceipt(String receiptId, ReceiptType receiptType, BigDecimal receiptAmount, Person person, Company company) {
        var fromAccount = accounts.get(person.getIban());

        if (fromAccount.getBalance().compareTo(receiptAmount) < 0) {
            receiptEventPublisher.publishDeclineReceiptEvent(receiptId, receiptAmount, company.getIdentifier(), receiptType, person.getIban());
            return;
        }

        transfer(person.getIban(), company.getIban(), receiptAmount);

        receiptEventPublisher.publishReceiptPayment(receiptId, receiptAmount, company.getIdentifier(), receiptType, person.getIban());
    }

    public String contractMortgage(String userIdentifier, String personIban, BigDecimal amount) {

        if (treasuryAccount.getBalance().compareTo(amount) < 0) {
            mortgageEventPublisher.publishRejectMortgage(userIdentifier, personIban, amount);

            throw new MortgageRejected();
        }

        var mortgageId = getNewMortgageId();

        mortgages.put(mortgageId, new Mortgage(mortgageId, userIdentifier, amount, personIban, getWorld()));

        var customerAccount = accounts.get(personIban);
        treasuryAccount.withdraw(amount);
        customerAccount.deposit(amount);

        mortgageEventPublisher.publishContractMortgage(userIdentifier, personIban, mortgageId, amount);

        return mortgageId;
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

        account.withdraw(installmentAmount);
        treasuryAccount.deposit(installmentAmount);
        mortgage.amortize(installmentAmount);
    }

    public Boolean isMortgageAmortized(String mortgageIdentifier) {
        return mortgages.get(mortgageIdentifier).isAmortized();
    }

    public void cancelMortgage(String mortgageIdentifier) {
        var mortgage = mortgages.remove(mortgageIdentifier);

        mortgageEventPublisher.publishCancelMortgage(mortgage.getUserIdentifier(), mortgage.getIban(), mortgageIdentifier);
    }

    private String getNewMortgageId() {
        var id = "MORTGAGE-" + mortgageCounter;
        mortgageCounter++;
        return id;
    }

    public Account getTreasuryAccount() {
        return treasuryAccount;
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

    private Integer ibanCounter = 0;

    private Integer panCounter = 0;

    private Integer mortgageCounter = 0;

    private ReceiptEventPublisher receiptEventPublisher;

    private AccountEventPublisher accountEventPublisher;

    private MortgageEventPublisher mortgageEventPublisher;

    private DebitCardEventPublisher debitCardEventPublisher;

    private UserEventPublisher userEventPublisher;

    private Account treasuryAccount;
}