package io.wasupu.boinet.eventPublisher;

public class PublishByEventTypeFactory {

    public static EventPublisher createEventTypePublisher(String streamServiceNamespace, String serverKeyStorePassphrase, String clientKeyStorePassphrase) {

        var streamByEventType = new PublisherByEventType();

        streamByEventType.register("bankBalance", new LogEventPublisher());

        streamByEventType.register("registerUser", new StreamEventPublisher("userRegistrations", streamServiceNamespace, serverKeyStorePassphrase, clientKeyStorePassphrase, 1000, 250));
        streamByEventType.register("contractCurrentAccount", new StreamEventPublisher("currentAccountContracts", streamServiceNamespace, serverKeyStorePassphrase, clientKeyStorePassphrase, 1000, 250));

        var currentAccountMovementsStream = new StreamEventPublisher("currentAccountMovements", streamServiceNamespace, serverKeyStorePassphrase, clientKeyStorePassphrase, 2500, 250);
        streamByEventType.register("deposit", currentAccountMovementsStream);
        streamByEventType.register("withdraw", currentAccountMovementsStream);

        streamByEventType.register("contractDebitCard", new StreamEventPublisher("debitCardContracts", streamServiceNamespace, serverKeyStorePassphrase, clientKeyStorePassphrase, 1000, 250));

        var debitCardMovementsStream = new StreamEventPublisher("debitCardMovements", streamServiceNamespace, serverKeyStorePassphrase, clientKeyStorePassphrase, 1500, 250);
        streamByEventType.register("acceptPayment", debitCardMovementsStream);
        streamByEventType.register("declinePayment", debitCardMovementsStream);

        var mortgageContractsStream = new StreamEventPublisher("mortgageContracts", streamServiceNamespace, serverKeyStorePassphrase, clientKeyStorePassphrase, 1000, 250);
        streamByEventType.register("contractMortgage", mortgageContractsStream);
        streamByEventType.register("cancelMortgage", mortgageContractsStream);
        streamByEventType.register("rejectMortgage", mortgageContractsStream);

        var mortgageInstallmentsStream = new StreamEventPublisher("mortgageInstallments", streamServiceNamespace, serverKeyStorePassphrase, clientKeyStorePassphrase, 1000, 250);
        streamByEventType.register("payMortgageInstallment", mortgageInstallmentsStream);
        streamByEventType.register("declineMortgageInstallment", mortgageInstallmentsStream);

        var receiptsStream = new StreamEventPublisher("receipts", streamServiceNamespace, serverKeyStorePassphrase, clientKeyStorePassphrase, 1000, 250);
        streamByEventType.register("acceptReceipt", receiptsStream);
        streamByEventType.register("declineReceipt", receiptsStream);

        return streamByEventType;
    }
}
