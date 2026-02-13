package com.tarrina.orders.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateDeliveryPartnerRequest {

    @NotNull
    private Long userId;

    private String vehicleType;

    private String vehicleNumber;

    private JsonNode vehicleRegistration;

    private Integer maxOrdersPerDay;

    private BigDecimal currentLatitude;

    private BigDecimal currentLongitude;

    private JsonNode metadata;

    private Long serviceZoneId;
}
