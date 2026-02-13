package com.tarrina.orders.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter

public class AssignmentResultDto {
    private Long orderId;
    private Long partnerUserId;
    private BigDecimal distanceKm;
    private BigDecimal score;
    private String reason;

    public AssignmentResultDto(
            Long orderId,
            Long partnerUserId,
            BigDecimal distanceKm,
            BigDecimal score,
            String reason
    ) {
        this.orderId = orderId;
        this.partnerUserId = partnerUserId;
        this.distanceKm = distanceKm;
        this.score = score;
        this.reason = reason;
    }

    //ADD THIS
    public static AssignmentResultDto noPartner(Long orderId, String reason) {
        return new AssignmentResultDto(
                orderId,
                null,
                null,
                null,
                reason
        );
    }

}
