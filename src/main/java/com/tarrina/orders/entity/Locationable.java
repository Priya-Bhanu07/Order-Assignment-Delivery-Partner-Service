package com.tarrina.orders.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "locationables")
@Getter
@Setter
public class Locationable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    /* Polymorphic */
    private String locationableType;   // ORDER, USER, PARTNER
    private Long locationableId;

    /* Context */
    private String addressId;
    private String purpose;             // DELIVERY, PICKUP, HOME
    private String type;
    private Boolean isPrimary;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String context;

    private LocalDateTime validFrom;
    private LocalDateTime validTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attached_by")
    private User attachedBy;

    private LocalDateTime attachedAt;
    private LocalDateTime visitedAt;

    private Integer version;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
