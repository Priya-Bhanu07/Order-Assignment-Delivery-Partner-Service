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
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID uuid;

    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;

    /* ===== REFERENCES ===== */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false)
    private Contact contact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_partner_id")
    private DeliveryPartner assignedPartner;

    /*DELIVERY LOCATION*/

    private String deliveryAddress;

    @Column(precision = 11, scale = 8)
    private BigDecimal deliveryLatitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal deliveryLongitude;

    @Column(columnDefinition = "GEOGRAPHY(POINT,4326)")
    private Point deliveryLocation;

    /* ORDER DETAILS  */

    @Column(columnDefinition = "jsonb", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode orderItems;

    private String description;
    private String priority;

    private BigDecimal estimatedWeight;
    private BigDecimal estimatedVolume;

    /* STATUS */

    private String status;
    private LocalDateTime assignedAt;
    private LocalDateTime pickedAt;
    private LocalDateTime dispatchedAt;
    private LocalDateTime deliveredAt;

    /* DISTANCE  */

    private BigDecimal distanceKm;
    private Integer estimatedDeliveryMinutes;

    /*  FINANCIAL  */

    private BigDecimal orderValue;
    private BigDecimal deliveryCharge;

    /* AUDIT */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    /*@Version
    private Long version;*/
    /* LIFECYCLE HOOKS  */

    @PrePersist
    public void prePersist() {

        this.uuid = UUID.randomUUID();

        if (this.orderNumber == null) {
            this.orderNumber = "ORD-" + UUID.randomUUID()
                    .toString()
                    .substring(0, 8)
                    .toUpperCase();
        }

        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;

        if (this.status == null) {
            this.status = "received";
        }

        // PostGIS point automatically if lat/long exist
        if (deliveryLatitude != null
                && deliveryLongitude != null
                && deliveryLocation == null) {

            GeometryFactory gf =
                    new GeometryFactory(new PrecisionModel(), 4326);

            this.deliveryLocation = gf.createPoint(
                    new Coordinate(
                            deliveryLongitude.doubleValue(),
                            deliveryLatitude.doubleValue()
                    )
            );
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
