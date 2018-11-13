package io.wasupu.boinet.financial.eventPublisher;

import io.wasupu.boinet.World;
import io.wasupu.boinet.companies.ReceiptType;
import io.wasupu.boinet.economicalSubjects.EconomicalSubject;

import java.math.BigDecimal;
import java.util.Map;

import static io.wasupu.boinet.financial.Money.convertMoneyToJson;

public class BankEventPublisher {

    public BankEventPublisher(World world){
        this.world = world;
    }

    public void publishReceiptPayment(String receiptId, BigDecimal amount, String companyIdentifier, ReceiptType receiptType) {
        world.getEventPublisher().publish(Map.of(
            "eventType", "acceptReceipt",
            "receiptId", receiptId,
            "amount", convertMoneyToJson(amount),
            "details", receiptType.toString().toLowerCase(),
            "company", companyIdentifier,
            "date", world.getCurrentDateTime().toDate()));
    }


    public void publishRegisterUserEvent(EconomicalSubject subject) {
        world.getEventPublisher().publish(Map.of(
            "eventType", "registerUser",
            "type", subject.getType().toString(),
            "user", subject.getIdentifier(),
            "date", world.getCurrentDateTime().toDate()));
    }

    private World world;
}
