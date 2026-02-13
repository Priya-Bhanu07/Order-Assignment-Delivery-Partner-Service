package com.tarrina.orders.dto;

import java.math.BigDecimal;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderRequest {
                @NotNull
                public Long contactId;

                @NotBlank
                public String deliveryAddress;

                @NotNull
                public BigDecimal deliveryLatitude;

                @NotNull
                public BigDecimal deliveryLongitude;

                @NotNull
                public JsonNode orderItems;

                @NotBlank
                public String priority;


}

