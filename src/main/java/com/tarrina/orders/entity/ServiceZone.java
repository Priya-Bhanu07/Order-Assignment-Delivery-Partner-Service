package com.tarrina.orders.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.Point;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "service_zones")
public class ServiceZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID uuid;

    private String zoneName;
    private String zoneCode;
    private String description;

    private BigDecimal centerLatitude;
    private BigDecimal centerLongitude;
    private BigDecimal areaSqkm;
    private Integer estimatedPopulation;

    private String status;
    private Boolean isPrimary;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String metadata;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;


    @Column(columnDefinition = "GEOMETRY(POLYGON,4326)")
    private Polygon boundary;

    @Column(columnDefinition = "GEOMETRY(POINT,4326)")
    private Point centerPoint;
}
