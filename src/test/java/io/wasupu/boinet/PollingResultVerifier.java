package io.wasupu.boinet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.lang.Thread.*;

public class PollingResultVerifier<T> {

    public static <T> PollingResultVerifier<T> retry(Supplier<T> pollingFunction) {
        return retry(200, 25, pollingFunction);
    }

    public static <T> PollingResultVerifier<T> retry(Integer pollingTime, Integer maxRetries, Supplier<T> pollingFunction) {
        return new PollingResultVerifier<>(pollingTime, maxRetries, pollingFunction);
    }

    public PollingResultVerifier(Integer pollingTime, Integer maxRetries, Supplier<T> pollingFunction) {
        this.pollingFunction = pollingFunction;
        this.pollingTime = pollingTime;
        this.maxRetries = maxRetries;
    }

    public PollingResultVerifier<T> subscribe(Consumer<T> assertion) {
        assertions.add(assertion);

        return this;
    }

    public void run() {
        runRecursive(maxRetries);
    }

    private void runRecursive(Integer remainingRetries) {
        logger.info(remainingRetries + " retries remaining");

        try {
            assertions.forEach(assertion -> assertion.accept(pollingFunction.get()));
        } catch (Throwable failure) {
            if (remainingRetries == 0) {
                throw failure;
            }

            try {
                sleep(pollingTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            runRecursive(remainingRetries - 1);
        }
    }

    private Collection<Consumer<T>> assertions = new ArrayList<>();

    private Supplier<T> pollingFunction;

    private Integer pollingTime;

    private Integer maxRetries;

    private static final Logger logger = LoggerFactory.getLogger(PollingResultVerifier.class);

}
