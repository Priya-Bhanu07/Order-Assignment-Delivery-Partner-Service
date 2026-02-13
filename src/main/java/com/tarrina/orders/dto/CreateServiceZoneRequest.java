package com.tarrina.orders.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class CreateServiceZoneRequest {
    private String zoneName;
    private String wktPolygon;

}