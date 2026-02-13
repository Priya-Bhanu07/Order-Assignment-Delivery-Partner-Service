package com.tarrina.orders.external.distance;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class DistanceCalculatorSerive implements DistanceCalculator {

    @Override
    public BigDecimal calculateDistanceKm(
            BigDecimal originLat,
            BigDecimal originLng,
            BigDecimal destinationLat,
            BigDecimal destinationLng
    ) {
        if (originLat == null || originLng == null ||
                destinationLat == null || destinationLng == null) {
            return BigDecimal.valueOf(9999);
        }

        double lat1 = originLat.doubleValue();
        double lon1 = originLng.doubleValue();
        double lat2 = destinationLat.doubleValue();
        double lon2 = destinationLng.doubleValue();

        final double EARTH_RADIUS = 6371.0;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = EARTH_RADIUS * c;

        return BigDecimal.valueOf(distance)
                .setScale(4, RoundingMode.HALF_UP);
    }
}