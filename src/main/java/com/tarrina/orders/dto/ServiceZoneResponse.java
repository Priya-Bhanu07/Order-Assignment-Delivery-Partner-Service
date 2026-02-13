package com.tarrina.orders.dto;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter

@Data
    public class ServiceZoneResponse {
        private Long id;
        private UUID uuid;
        private String zoneName;
        private String boundaryWkt;
    }