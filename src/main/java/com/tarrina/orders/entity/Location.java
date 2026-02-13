package com.tarrina.orders.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "locations")
@Getter
@Setter
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID uuid;

    /* OWNER */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /*  ADDRESS*/
    private String addressId;

    @Column(columnDefinition = "TEXT")
    private String address;

    private String formattedAddress;
    private String attention;

    private String street;
    private String street2;
    private String city;
    private String state;
    private String zip;
    private String country;
    private String postalCode;

    /* COORDINATES*/
    private BigDecimal latitude;
    private BigDecimal longitude;

    @Column(columnDefinition = "GEOGRAPHY(POINT,4326)")
    private org.locationtech.jts.geom.Point location;

    @Column(columnDefinition = "GEOGRAPHY(POINT,4326)")
    private org.locationtech.jts.geom.Point coordinates;

    @Column(columnDefinition = "GEOMETRY(POINT,3857)")
    private org.locationtech.jts.geom.Point coordinatesMercator;

    /* ========= DEVICE ========= */
    private BigDecimal deviceLatitude;
    private BigDecimal deviceLongitude;

    @Column(columnDefinition = "GEOGRAPHY(POINT,4326)")
    private org.locationtech.jts.geom.Point deviceLocation;

    /*ACCURACY*/
    private BigDecimal accuracy;
    private BigDecimal horizontalAccuracy;
    private BigDecimal altitude;
    private Float altitudeAccuracy;
    private Float heading;
    private Float speed;
    private Short satellites;

    /* GEOCODING */
    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String structuredAddress;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String addressComponents;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String reverseGeocode;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String routingMetadata;

    private String placeId;
    private String plusCode;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String types;

    /* SOURCE */
    private String sourceType;
    private String devicePlatform;
    private String deviceId;
    private String sessionId;

    private String timezone;

    /*VERIFICATION*/
    private Boolean locationVerified;
    private String verificationMethod;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String verificationData;

    /*TREE */
    private Integer sequenceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Location parent;

    /* AUDIT */
    private String status;
    private LocalDateTime capturedAt;
    private LocalDateTime processedAt;
    private LocalDateTime expiresAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
