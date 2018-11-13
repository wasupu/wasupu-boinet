package io.wasupu.boinet.financial.eventPublisher;

import io.wasupu.boinet.World;
import io.wasupu.boinet.companies.ReceiptType;

import java.math.BigDecimal;
import java.util.Map;

import static io.wasupu.boinet.financial.Money.convertMoneyToJson;

public class ReceiptEventPublisher {

    public ReceiptEventPublisher(World world){
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

    private World world;
}
