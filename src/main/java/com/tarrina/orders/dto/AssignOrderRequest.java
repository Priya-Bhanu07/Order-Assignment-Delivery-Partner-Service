package com.tarrina.orders.dto;
import jakarta.validation.constraints.NotNull;

public class AssignOrderRequest {

    @NotNull
    private Long orderId;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
