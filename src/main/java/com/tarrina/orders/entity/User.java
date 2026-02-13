package com.tarrina.orders.entity;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    /* PRIMARY KEY */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID uuid;

    /* BASIC INFO  */

    private String name;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String firstName;
    private String lastName;
    private String salutation;

    /* LOCATION  */

  //  private Double latitude;
  private String address;
    private String streetAddress;
    private String city;
    private String state;
    private String country;
    private String pincode;

    /*  WORK DETAILS */

    private Long roleId;
    private String department;
    private String designation;

    private LocalDateTime dateOfJoining;

    private BigDecimal monthlyTarget;

    /*STATUS  */

    private String status;

    private Boolean isDeactivated;

    private LocalDateTime lastLogin;
    private LocalDateTime lastTrackedAt;

    private Boolean hasActiveSession;

    /* JSON FIELDS*/

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode applicationSettings;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode communicationPreferences;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode metadata;

    /*TIMESTAMPS*/

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @PrePersist
    public void prePersist() {
        this.uuid = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
    }

}

