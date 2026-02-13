package com.tarrina.orders.entity;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "delivery_partners")
public class DeliveryPartner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String vehicleType;
    private String vehicleNumber;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String vehicleRegistration;

    private Integer vehicleCapacityItems;
    private BigDecimal vehicleCapacityWeight;
    private BigDecimal vehicleCapacityVolume;

    private Integer maxOrdersPerDay;
    private Integer currentActiveOrders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_location_id")
    private Location warehouseLocation;

    private String availabilityStatus;
    private LocalDateTime lastStatusUpdate;
    private LocalDateTime onLeaveUntil;
    private String leaveReason;

    /* LOCATION TRACKING*/

    @Column(precision = 11, scale = 8)
    private BigDecimal currentLatitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal currentLongitude;

    @Column(columnDefinition = "GEOGRAPHY(POINT,4326)")
    private Point currentLocation;

    private LocalDateTime locationUpdatedAt;

    /* ======= METRICS ======= */

    private Integer totalDeliveries;
    private Integer successfulDeliveries;
    private Integer failedDeliveries;
    private Integer averageDeliveryTimeMinutes;
    private BigDecimal rating;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode metadata;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_zone_id")
    private ServiceZone serviceZone;
  /*  @Version
    private Long version;*/
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;

        if (this.currentActiveOrders == null) {
            this.currentActiveOrders = 0;
        }
        if (this.availabilityStatus == null) {
            this.availabilityStatus = "available";
        }
        if (this.lastStatusUpdate == null) {
            this.lastStatusUpdate = LocalDateTime.now();
        }

        //Auto-create PostGIS point if lat/long exist
        if (currentLatitude != null
                && currentLongitude != null
                && currentLocation == null) {

            GeometryFactory gf =
                    new GeometryFactory(new PrecisionModel(), 4326);

            this.currentLocation = gf.createPoint(
                    new Coordinate(
                            currentLongitude.doubleValue(),
                            currentLatitude.doubleValue()
                    )
            );
            this.locationUpdatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();

        // update point if coordinates changed
        if (currentLatitude != null
                && currentLongitude != null) {

            GeometryFactory gf =
                    new GeometryFactory(new PrecisionModel(), 4326);

            this.currentLocation = gf.createPoint(
                    new Coordinate(
                            currentLongitude.doubleValue(),
                            currentLatitude.doubleValue()
                    )
            );
            this.locationUpdatedAt = LocalDateTime.now();
        }
    }
}
