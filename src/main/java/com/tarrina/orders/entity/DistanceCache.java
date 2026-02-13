package com.tarrina.orders.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "distance_cache",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"originLat", "originLng", "destLat", "destLng"}
        )
)
public class DistanceCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(precision = 11, scale = 8)
    private BigDecimal originLat;
    @Column(precision = 11, scale = 8)
    private BigDecimal originLng;
    @Column(precision = 11, scale = 8)
    private BigDecimal destLat;
    @Column(precision = 11, scale = 8)
    private BigDecimal destLng;
    private BigDecimal distanceKm;
    private LocalDateTime createdAt = LocalDateTime.now();
}
