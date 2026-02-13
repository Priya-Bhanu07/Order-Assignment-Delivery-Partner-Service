package com.tarrina.orders.repository;
import com.tarrina.orders.entity.ServiceZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ServiceZoneRepository extends JpaRepository<ServiceZone, Long> {

    @Query(value = """
        SELECT * FROM service_zones sz
        WHERE ST_Contains(
            sz.zone_polygon,
            ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)
        )
        LIMIT 1
    """, nativeQuery = true)
    Optional<ServiceZone> findZoneByLatLng(double lat, double lng);


    @Query(value = """
    SELECT EXISTS (
        SELECT 1
        FROM service_zones sz
        JOIN delivery_partners dp
          ON dp.service_zone_id = sz.id
        WHERE dp.id = :partnerId
          AND sz.deleted_at IS NULL
          AND ST_Contains(
                sz.boundary,
                ST_SetSRID(
                    ST_MakePoint(:longitude, :latitude),
                    4326
                )
          )
    )
""", nativeQuery = true)
    boolean isOrderInsidePartnerZone(
            Long partnerId,
            Double longitude,   // ✅ FIRST
            Double latitude     // ✅ SECOND
    );
}
