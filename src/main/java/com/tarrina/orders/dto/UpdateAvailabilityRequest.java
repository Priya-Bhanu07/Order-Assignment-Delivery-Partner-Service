package com.tarrina.orders.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateAvailabilityRequest {

    @NotBlank
    public String status;

    public LocalDateTime onLeaveUntil;
    public String leaveReason;// available, busy, on_leave, offline
}
