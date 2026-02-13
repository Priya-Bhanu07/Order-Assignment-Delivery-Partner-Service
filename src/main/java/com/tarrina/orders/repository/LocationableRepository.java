package com.tarrina.orders.repository;

import com.tarrina.orders.entity.Location;
import com.tarrina.orders.entity.Locationable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LocationableRepository
        extends JpaRepository<Locationable, Long> {

    @Query("""
        SELECT l.location
        FROM Locationable l
        WHERE l.locationableType = :type
          AND l.locationableId = :id
          AND l.purpose = :purpose
          AND l.isPrimary = true
          AND l.validTo IS NULL
    """)
    Optional<Location> findPrimaryLocation(
            String type,
            Long id,
            String purpose
    );
}
