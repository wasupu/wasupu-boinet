package io.wasupu.boinet.eventPublisher;

public class StreamByEventTypeFactory {

    public static EventPublisher createEventTypePublisher(String streamServiceApiKey, String streamServiceNamespace) {

        var streamByEventType = new StreamByEventType();
        streamByEventType.register("registerUser", new StreamEventPublisher("userRegistrations", streamServiceApiKey, streamServiceNamespace));
        streamByEventType.register("contractCurrentAccount", new StreamEventPublisher("currentAccountContracts", streamServiceApiKey, streamServiceNamespace));

        var currentAccountMovementsStream = new StreamEventPublisher("currentAccountMovements", streamServiceApiKey, streamServiceNamespace);
        streamByEventType.register("deposit", currentAccountMovementsStream);
        streamByEventType.register("withdraw", currentAccountMovementsStream);

        streamByEventType.register("contractDebitCard", new StreamEventPublisher("debitCardContracts", streamServiceApiKey, streamServiceNamespace));

        var debitCardMovementsStream = new StreamEventPublisher("debitCardMovements", streamServiceApiKey, streamServiceNamespace);
        streamByEventType.register("acceptPayment", debitCardMovementsStream);
        streamByEventType.register("declinePayment", debitCardMovementsStream);

        var mortgageContractsStream = new StreamEventPublisher("mortgageContracts", streamServiceApiKey, streamServiceNamespace);
        streamByEventType.register("contractMortgage", mortgageContractsStream);
        streamByEventType.register("cancelMortgage", mortgageContractsStream);
        streamByEventType.register("rejectMortgage", mortgageContractsStream);

        var mortgageInstallmentsStream = new StreamEventPublisher("mortgageInstallments", streamServiceApiKey, streamServiceNamespace);
        streamByEventType.register("payMortgageInstallment", mortgageInstallmentsStream);
        streamByEventType.register("declineMortgageInstallment", mortgageInstallmentsStream);

        var receiptsStream = new StreamEventPublisher("receipts", streamServiceApiKey, streamServiceNamespace);
        streamByEventType.register("acceptReceipt", receiptsStream);
        streamByEventType.register("declineReceipt", receiptsStream);

        return streamByEventType;
    }
}
