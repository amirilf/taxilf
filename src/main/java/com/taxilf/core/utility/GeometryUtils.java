package com.taxilf.core.utility;

import java.util.Random;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

public class GeometryUtils {
    
    private static final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    private static final Random random = new Random();

    public static Point randomPointInMashhad() {
        double lat = Variables.MASHHAD_LAT + (random.nextDouble() - 0.5) * Variables.RADIUS_IN_DEGREES;
        double lon = Variables.MASHHAD_LON + (random.nextDouble() - 0.5) * Variables.RADIUS_IN_DEGREES;
        return geometryFactory.createPoint(new Coordinate(lon, lat));
    }

    public static double calculateFare(Point start, Point end) {
        
        double lat1 = start.getY();
        double lon1 = start.getX();
        double lat2 = end.getY();
        double lon2 = end.getX();

        double distanceInMeters = haversineDistance(lat1, lon1, lat2, lon2);
        double fare = Variables.PRICE_PER_METERS * distanceInMeters;

        return Math.round(fare * 10.0) / 10.0;    
    }

    private static double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return Variables.EARTH_RADIUS * c; // Distance in meters
    }

}
