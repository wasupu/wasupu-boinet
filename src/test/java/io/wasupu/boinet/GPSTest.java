package io.wasupu.boinet;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

public class GPSTest {

    @Test
    public void shouldReturnANotNullPairOfCoordinates() {
        Pair<Double, Double> coordinates = gps.coordinates();
        assertNotNull("The coordinates must be not null", coordinates);
    }

    @Test
    public void shouldReturnCoordinatesAroundALocation() {
        IntStream.range(0, 10000).forEach(i -> {
            Pair<Double, Double> coordinates = gps.coordinates();

            assertThat(coordinates.getLeft())
                .as("The latitude must be in range")
                .isBetween(40.30,40.53);

            assertThat(coordinates.getRight())
                .as("The longitude must be in range")
                .isBetween(-3.85,-3.5);

        });
    }

    private GPS gps = new GPS();
}
