package io.wasupu.boinet;

import io.wasupu.boinet.eventPublisher.LogEventPublisher;
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
            new StreamEventPublisher(CARD_STREAM_ID, streamServiceApiKey.get(), streamServiceNamespace.get()) :
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

    private static final String CARD_STREAM_ID = "cardMovementEventStream";
}


