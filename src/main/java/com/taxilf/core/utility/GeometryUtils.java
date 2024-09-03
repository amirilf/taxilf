package com.taxilf.core.utility;

import java.util.Random;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

public class GeometryUtils {
    
    private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    private static final Random random = new Random();

    public static Point generateRandomPointInMashhad() {
        double radiusInDegrees = 0.05; // ~5km
        double lat = 36.2605 + (random.nextDouble() - 0.5) * radiusInDegrees;
        double lon = 59.6168 + (random.nextDouble() - 0.5) * radiusInDegrees;
        return geometryFactory.createPoint(new Coordinate(lon, lat));
    }

}
