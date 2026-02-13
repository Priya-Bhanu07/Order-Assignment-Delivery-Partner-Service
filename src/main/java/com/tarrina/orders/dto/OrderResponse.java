package com.tarrina.orders.dto;

import com.tarrina.orders.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
public class OrderResponse {
        public Long id;
        public UUID uuid;
        public String status;
        public Long assignedPartnerUserId;

}
