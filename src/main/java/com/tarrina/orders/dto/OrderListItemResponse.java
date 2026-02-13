package com.tarrina.orders.dto;

import com.tarrina.orders.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class OrderListItemResponse {
    private Long id;
    private UUID uuid;
    private String status;
    private Long assignedPartnerUserId;

    public static OrderListItemResponse from(Order order) {
        return new OrderListItemResponse(
                order.getId(),
                order.getUuid(),
                order.getStatus(),
                order.getAssignedPartner() != null
                        ? order.getAssignedPartner().getId()
                        : null
        );
    }
}
