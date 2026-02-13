package com.tarrina.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderStatusResponse {
        private Long id;
        private String previousStatus;
        private String currentStatus;
        private String changedAt;
}
