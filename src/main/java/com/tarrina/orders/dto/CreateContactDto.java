package com.tarrina.orders.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateContactDto {
        @NotBlank
        private String name;

        @NotBlank
        private String phone;

        private String email;
    }
