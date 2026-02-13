package com.tarrina.orders.service;
import com.tarrina.orders.dto.AssignmentResultDto;


import com.tarrina.orders.dto.AssignmentResultDto;
import com.tarrina.orders.entity.DeliveryPartner;

public interface OrderAssignmentService {

    AssignmentResultDto assignOrder(Long orderId);

    AssignmentResultDto reassignOrder(Long orderId, String reason);

    void handlePartnerOnLeave(DeliveryPartner partner);
}


