package io.wasupu.boinet;

import com.spatial4j.core.context.SpatialContext;
import com.spatial4j.core.shape.Point;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Random;

public class GPS {

    public Pair<Double, Double> coordinates() {
        double distanceInDegrees = -0.1 + random.nextDouble() * 0.2;
        double bearingInDegrees = random.nextDouble() * 360;

        Point point = SpatialContext.GEO
            .getDistCalc()
            .pointOnBearing(SpatialContext.GEO
                    .makePoint(latitude,longitude),
                distanceInDegrees,
                bearingInDegrees,
                SpatialContext.GEO,
                SpatialContext.GEO.makePoint(0, 0));


        return Pair.of(point.getX(), point.getY());
    }

    private double latitude = 40.416657;
    private double longitude = -3.703502;

    private Random random = new Random();
}
