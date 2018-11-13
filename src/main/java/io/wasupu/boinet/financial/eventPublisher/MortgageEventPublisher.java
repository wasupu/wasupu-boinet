package io.wasupu.boinet.financial.eventPublisher;

import io.wasupu.boinet.World;
import io.wasupu.boinet.financial.Mortgage;

import java.math.BigDecimal;
import java.util.Map;

import static io.wasupu.boinet.financial.Money.convertMoneyToJson;

public class MortgageEventPublisher {

    public MortgageEventPublisher(World world){
        this.world = world;
    }

    public void publishAmortization(BigDecimal amount, Mortgage mortgage) {
        world.getEventPublisher().publish(Map.of(
            "eventType", "payMortgageInstallment",
            "mortgageIdentifier", mortgage.getIdentifier(),
            "iban", mortgage.getIban(),
            "totalAmount", convertMoneyToJson(mortgage.getTotalAmount()),
            "installmentAmount", convertMoneyToJson(amount),
            "totalAmortizedAmount", convertMoneyToJson(mortgage.getAmortizedAmount()),
            "date", world.getCurrentDateTime().toDate()));
    }

    public void publishDeclineMortgageInstallment(Mortgage mortgage, BigDecimal installmentAmount) {
        world.getEventPublisher().publish(Map.of(
            "eventType", "declineMortgageInstallment",
            "mortgageIdentifier", mortgage.getIdentifier(),
            "iban", mortgage.getIban(),
            "totalAmount", convertMoneyToJson(mortgage.getTotalAmount()),
            "installmentAmount", convertMoneyToJson(installmentAmount),
            "totalAmortizedAmount", convertMoneyToJson(mortgage.getAmortizedAmount()),
            "date", world.getCurrentDateTime().toDate()));
    }

    public void publishCancelMortgage(String userIdentifier, String iban, String mortgageIdentifier) {
        world.getEventPublisher().publish(Map.of(
            "eventType", "cancelMortgage",
            "iban", iban,
            "mortgageIdentifier", mortgageIdentifier,
            "user", userIdentifier,
            "date", world.getCurrentDateTime().toDate()));
    }

    public void publishContractMortgage(String identifier, String iban, String mortgageIdentifier, BigDecimal amount) {
        world.getEventPublisher().publish(Map.of(
            "eventType", "contractMortgage",
            "iban", iban,
            "mortgageAmount", convertMoneyToJson(amount),
            "mortgageIdentifier", mortgageIdentifier,
            "user", identifier,
            "date", world.getCurrentDateTime().toDate()));
    }

    public void publishRejectMortgage(String userIdentifier, BigDecimal amount) {
        world.getEventPublisher().publish(Map.of(
            "eventType", "rejectMortgage",
            "mortgageAmount", convertMoneyToJson(amount),
            "user", userIdentifier,
            "date", world.getCurrentDateTime().toDate()));
    }

    private World world;
}
