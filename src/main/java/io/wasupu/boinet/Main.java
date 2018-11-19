package io.wasupu.boinet;

import io.wasupu.boinet.eventPublisher.EventPublisher;
import io.wasupu.boinet.eventPublisher.LogEventPublisher;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

import static io.wasupu.boinet.eventPublisher.PublishByEventTypeFactory.createEventTypePublisher;


public class Main {

    /**
     * @param commandLineArgs --population = Required. Number of people.
     *                        --companies = Required. Number of companies.
     *                        --seed-capital = Required. Bank seed capital.
     *                        --number-of-ticks = Optional. If not specified, there will be infinite ticks.
     *                        --stream-service-namespace = Optional full namespace for stream service (full url).
     *                        --server-client-store-passphrase = Optional. Required if --stream-service-namespace is defined
     *                        --server-key-store-passphrase = Optional. Required if --stream-service-namespace is defined
     */
    public static void main(String[] commandLineArgs) {
        var numberOfPeople = findArgument("--population", commandLineArgs)
            .map(Integer::new)
            .orElseThrow(() -> new IllegalArgumentException("--population argument required"));

        var numberOfCompanies = findArgument("--companies", commandLineArgs)
            .map(Integer::new)
            .orElseThrow(() -> new IllegalArgumentException("--companies argument required"));

        var seedCapital = findArgument("--seed-capital", commandLineArgs)
            .map(BigDecimal::new)
            .orElseThrow(() -> new IllegalArgumentException("--seed-capital argument required"));

        var eventPublisher = findArgument("--stream-service-namespace", commandLineArgs)
            .map(createStreamServiceEventPublisher(commandLineArgs))
            .orElse(new LogEventPublisher());

        var world = new World(eventPublisher, seedCapital);
        world.init(numberOfPeople, numberOfCompanies);
        world.start(findArgument("--number-of-ticks", commandLineArgs).map(Integer::new));
    }

    private static Function<String, EventPublisher> createStreamServiceEventPublisher(String[] commandLineArgs) {
        return streamServiceNamespace -> {
            var serverKeyStorePassphrase = findArgument("--server-key-store-passphrase", commandLineArgs)
                .map(String::valueOf)
                .orElseThrow(() -> new IllegalArgumentException("--server-key-store-passphrase argument required"));

            var clientKeyStorePassphrase = findArgument("--client-key-store-passphrase", commandLineArgs)
                .map(String::valueOf)
                .orElseThrow(() -> new IllegalArgumentException("--client-key-store-passphrase argument required"));

            return createEventTypePublisher(streamServiceNamespace, serverKeyStorePassphrase, clientKeyStorePassphrase);
        };
    }

    private static Optional<String> findArgument(String argumentName, String[] commandLineArgs) {
        return Arrays.stream(commandLineArgs)
            .filter(argument -> argument.startsWith(argumentName))
            .map(argument -> argument.replaceFirst(argumentName + "=", ""))
            .filter(argumentValue -> !argumentValue.isEmpty())
            .findFirst();
    }
}


