package io.wasupu.boinet;

import com.spatial4j.core.context.SpatialContext;
import com.spatial4j.core.shape.Point;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Random;

public class GPS {

    public Pair<Double, Double> coordinates() {
        return coordinatesAroundInRange(latitude, longitude, -0.1, 0.1);
    }

    public Pair<Double, Double> coordinatesAround(Double startLatitude, Double startLongitude) {
        return coordinatesAroundInRange(startLatitude, startLongitude, -0.00001, 0.00001);
    }

    private Pair<Double, Double> coordinatesAroundInRange(Double startLatitude,
                                                          Double startLongitude,
                                                          Double minRange,
                                                          Double maxRange) {
        var distanceInDegrees = getDistanceInDegrees(minRange, maxRange);
        var bearingInDegrees = random.nextDouble() * 360;

        var point = SpatialContext.GEO
            .getDistCalc()
            .pointOnBearing(SpatialContext.GEO.makePoint(startLatitude, startLongitude),
                distanceInDegrees,
                bearingInDegrees,
                SpatialContext.GEO,
                SpatialContext.GEO.makePoint(0, 0));

        return Pair.of(point.getX(), point.getY());
    }

    private double getDistanceInDegrees(Double minRange, Double maxRange) {
        return minRange + random.nextDouble() * (maxRange - minRange);
    }

    // Cuenca coordinates
    private double latitude = 40.073417;
    private double longitude = -2.137937;

    private Random random = new Random();
}
