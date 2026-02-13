package com.tarrina.orders.external.distance;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Primary
public class GoogleMapsDistanceCalculator implements DistanceCalculator {


    private final GoogleDistanceMatrixService googleService;
        private final HaversineUtil haversine;

        public GoogleMapsDistanceCalculator(
                GoogleDistanceMatrixService googleService,
                HaversineUtil haversine
        ) {
            this.googleService = googleService;
            this.haversine = haversine;
        }

        @Override
        public BigDecimal calculateDistanceKm(
                BigDecimal oLat,
                BigDecimal oLng,
                BigDecimal dLat,
                BigDecimal dLng
        ) {
            try {
                // Google first
                return googleService.getDistanceKm(oLat, oLng, dLat, dLng);
            } catch (Exception e) {
                // FALLBACK
                return haversine.calculate(oLat, oLng, dLat, dLng);
            }
        }
    }


