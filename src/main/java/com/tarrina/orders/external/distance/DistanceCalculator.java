package com.tarrina.orders.external.distance;
import java.math.BigDecimal;

public interface DistanceCalculator {

    /**
     * @return driving distance in kilometers
     */
    BigDecimal calculateDistanceKm(
            BigDecimal originLat,
            BigDecimal originLng,
            BigDecimal destinationLat,
            BigDecimal destinationLng
    );


}
