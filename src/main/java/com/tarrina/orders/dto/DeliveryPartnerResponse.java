package com.tarrina.orders.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class DeliveryPartnerResponse {

        private Long id;
        private Long userId;
        private String userName;

        private String vehicleType;
        private String vehicleNumber;
        private String vehicleRegistration;

        private String availabilityStatus;
        private Integer currentActiveOrders;
        private Integer maxOrdersPerDay;
        private LocalDateTime lastStatusUpdate;

        /** JSON fields */
        private JsonNode serviceZones;
        private JsonNode metadata;

        /** LOCATION*/
        private BigDecimal currentLatitude;
        private BigDecimal currentLongitude;
        private LocalDateTime locationUpdatedAt;

        /** PERFORMANCE METRICS*/
        private Integer totalDeliveries;
        private Integer successfulDeliveries;
        private Integer failedDeliveries;
        private Integer averageDeliveryTimeMinutes;
        private BigDecimal rating;

        /** LEAVE / AVAILABILITY DETAILS */
        private LocalDateTime onLeaveUntil;
        private String leaveReason;

        /** AUDIT */
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
}
