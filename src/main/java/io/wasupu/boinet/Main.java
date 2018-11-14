package io.wasupu.boinet;

import io.wasupu.boinet.eventPublisher.EventPublisher;
import io.wasupu.boinet.eventPublisher.LogEventPublisher;
import io.wasupu.boinet.eventPublisher.StreamByEventType;
import io.wasupu.boinet.eventPublisher.StreamEventPublisher;

import java.util.Arrays;
import java.util.Optional;

public class Main {

    /**
     * @param args --population = Required. Number of people.
     *             --companies = Required. Number of companies.
     *             --number-of-ticks = Optional. If not specified, there will be infinite ticks
     *             --stream-service-api-key = Optional. Required if --stream-service-namespace is defined
     *             --stream-service-namespace = Optional. Required if --stream-service-api-key is defined
     */
    public static void main(String[] args) {
        var numberOfPeople = findArgument("--population", args)
            .map(Integer::new)
            .orElseThrow(() -> new IllegalArgumentException("--population argument required"));
        var numberOfCompanies = findArgument("--companies", args)
            .map(Integer::new)
            .orElseThrow(() -> new IllegalArgumentException("--companies argument required"));

        var streamServiceApiKey = findArgument("--stream-service-api-key", args);
        var streamServiceNamespace = findArgument("--stream-service-namespace", args);

        var eventPublisher = (streamServiceApiKey.isPresent() && streamServiceNamespace.isPresent()) ?
            createEventTypePublisher(streamServiceApiKey.get(), streamServiceNamespace.get()) :
            new LogEventPublisher();

        var world = new World(eventPublisher);
        world.init(numberOfPeople, numberOfCompanies);
        world.start(findArgument("--number-of-ticks", args).map(Integer::new));
    }

    private static Optional<String> findArgument(String argumentName, String[] args) {
        return Arrays.stream(args)
            .filter(argument -> argument.startsWith(argumentName))
            .map(argument -> argument.replaceFirst(argumentName + "=", ""))
            .filter(argumentValue -> !argumentValue.isEmpty())
            .findFirst();
    }

    private static EventPublisher createEventTypePublisher(String streamServiceApiKey, String streamServiceNamespace) {

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


