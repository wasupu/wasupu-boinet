package io.wasupu.boinet.eventPublisher;

public class StreamByEventTypeFactory {

    public static EventPublisher createEventTypePublisher(String streamServiceNamespace, String serverKeyStorePassphrase, String clientKeyStorePassphrase) {

        var streamByEventType = new PublisherByEventType();
        streamByEventType.register("registerUser", new StreamEventPublisher("userRegistrations", streamServiceNamespace, serverKeyStorePassphrase, clientKeyStorePassphrase));
        streamByEventType.register("contractCurrentAccount", new StreamEventPublisher("currentAccountContracts", streamServiceNamespace, serverKeyStorePassphrase, clientKeyStorePassphrase));

        var currentAccountMovementsStream = new StreamEventPublisher("currentAccountMovements", streamServiceNamespace, serverKeyStorePassphrase, clientKeyStorePassphrase);
        streamByEventType.register("deposit", currentAccountMovementsStream);
        streamByEventType.register("withdraw", currentAccountMovementsStream);

        streamByEventType.register("contractDebitCard", new StreamEventPublisher("debitCardContracts", streamServiceNamespace, serverKeyStorePassphrase, clientKeyStorePassphrase));

        var debitCardMovementsStream = new StreamEventPublisher("debitCardMovements", streamServiceNamespace, serverKeyStorePassphrase, clientKeyStorePassphrase);
        streamByEventType.register("acceptPayment", debitCardMovementsStream);
        streamByEventType.register("declinePayment", debitCardMovementsStream);

        var mortgageContractsStream = new StreamEventPublisher("mortgageContracts", streamServiceNamespace, serverKeyStorePassphrase, clientKeyStorePassphrase);
        streamByEventType.register("contractMortgage", mortgageContractsStream);
        streamByEventType.register("cancelMortgage", mortgageContractsStream);
        streamByEventType.register("rejectMortgage", mortgageContractsStream);

        var mortgageInstallmentsStream = new StreamEventPublisher("mortgageInstallments", streamServiceNamespace, serverKeyStorePassphrase, clientKeyStorePassphrase);
        streamByEventType.register("payMortgageInstallment", mortgageInstallmentsStream);
        streamByEventType.register("declineMortgageInstallment", mortgageInstallmentsStream);

        var receiptsStream = new StreamEventPublisher("receipts", streamServiceNamespace, serverKeyStorePassphrase, clientKeyStorePassphrase);
        streamByEventType.register("acceptReceipt", receiptsStream);
        streamByEventType.register("declineReceipt", receiptsStream);

        return streamByEventType;
    }
}
