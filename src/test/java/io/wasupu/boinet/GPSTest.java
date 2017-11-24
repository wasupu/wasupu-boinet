package io.wasupu.boinet;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import static java.util.stream.IntStream.range;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

public class GPSTest {

    @Test
    public void shouldReturnANotNullPairOfCoordinates() {
        Pair<Double, Double> coordinates = gps.coordinates();
        assertNotNull("The coordinates must be not null", coordinates);
    }

    @Test
    public void shouldReturnCoordinatesAroundACenterLocation() {
        range(0, 10000).forEach(i -> {
            Pair<Double, Double> coordinates = gps.coordinatesAround(40.368305,-3.698323);

            assertThat(coordinates.getLeft())
                .as("The latitude must be in range")
                .isBetween(40.3682,40.3684);

            assertThat(coordinates.getRight())
                .as("The longitude must be in range")
                .isBetween(-3.6984,-3.6982);

        });
    }
    @Test
    public void shouldReturnCoordinatesAroundACenterGivenLocation() {
        range(0, 10000).forEach(i -> {
            Pair<Double, Double> coordinates = gps
                .coordinatesAround(new Double(40.405068652339665), new Double(-3.7093897480241935));

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
