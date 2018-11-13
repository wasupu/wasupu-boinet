package io.wasupu.boinet.financial.eventPublisher;

import io.wasupu.boinet.World;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.util.Map;

import static io.wasupu.boinet.financial.Money.convertMoneyToJson;

public class DebitCardEventPublisher {

    public DebitCardEventPublisher(World world){
        this.world = world;
    }

    public void publishContractDebitCard(String identifier, String iban, String panAsString) {
        world.getEventPublisher().publish(Map.of(
            "eventType", "contractDebitCard",
            "iban", iban,
            "pan", panAsString,
            "user", identifier,
            "date", world.getCurrentDateTime().toDate()));
    }

    public void publishCardPayment(BigDecimal amount, String pan, String companyIdentifier, String details, Pair<Double, Double> coordinates) {
        world.getEventPublisher().publish(Map.of(
            "eventType", "acceptPayment",
            "pan", pan,
            "amount", convertMoneyToJson(amount),
            "details", details,
            "geolocation", Map.of(
                "latitude", coordinates.getLeft(),
                "longitude", coordinates.getRight()),
            "company", companyIdentifier,
            "date", world.getCurrentDateTime().toDate()));
    }

    public void publishDeclineCardPayment(BigDecimal amount, String pan, String companyIdentifier, String details, Pair<Double, Double> coordinates) {
        world.getEventPublisher().publish(Map.of(
            "eventType", "declinePayment",
            "pan", pan,
            "amount", convertMoneyToJson(amount),
            "details", details,
            "geolocation", Map.of(
                "latitude", coordinates.getLeft(),
                "longitude", coordinates.getRight()),
            "company", companyIdentifier,
            "date", world.getCurrentDateTime().toDate()));
    }

    private World world;
}
