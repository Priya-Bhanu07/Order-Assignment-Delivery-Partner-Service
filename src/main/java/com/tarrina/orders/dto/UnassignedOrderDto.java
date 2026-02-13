package com.tarrina.orders.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnassignedOrderDto {
    private Long orderId;
    private long unassignedSinceMinutes;

    public UnassignedOrderDto(Long orderId, long unassignedSinceMinutes) {
        this.orderId = orderId;
        this.unassignedSinceMinutes = unassignedSinceMinutes;
    }

    public Long getOrderId() {
        return orderId;
    }

    public long getUnassignedSinceMinutes() {
        return unassignedSinceMinutes;
    }
}
