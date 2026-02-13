package com.tarrina.orders.repository;

import com.tarrina.orders.entity.DistanceCache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.Optional;

public interface DistanceCacheRepository
        extends JpaRepository<DistanceCache, Long> {

    Optional<DistanceCache> findByOriginLatAndOriginLngAndDestLatAndDestLng(
            BigDecimal oLat,
            BigDecimal oLng,
            BigDecimal dLat,
            BigDecimal dLng
    );
}
