package com.tarrina.orders.dto;

import com.tarrina.orders.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class OrderDetailsResponse {

    public Long id;
    public UUID uuid;
    public String status;
    public Long assignedPartnerUserId;

    public String deliveryAddress;
    public BigDecimal deliveryLatitude;
    public BigDecimal deliveryLongitude;

    public String priority;

    public LocalDateTime createdAt;
    public LocalDateTime assignedAt;

    public OrderDetailsResponse() {

    }

    public static OrderDetailsResponse from(Order order) {

        OrderDetailsResponse res = new OrderDetailsResponse();
        res.id = order.getId();
        res.uuid = order.getUuid();
        res.status = order.getStatus();
        res.assignedPartnerUserId =
                order.getAssignedPartner() != null
                        ? order.getAssignedPartner().getId()
                        : null;

        res.deliveryAddress = order.getDeliveryAddress();
        res.deliveryLatitude = order.getDeliveryLatitude();
        res.deliveryLongitude = order.getDeliveryLongitude();

        res.priority = order.getPriority();
        res.createdAt = order.getCreatedAt();
        res.assignedAt = order.getAssignedAt();

        return res;
    }
}
