package com.tarrina.orders.service;
import com.tarrina.orders.entity.DeliveryPartner;
import com.tarrina.orders.entity.Order;
import com.tarrina.orders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartnerLeaveService {

    private final OrderRepository orderRepository;
    private final OrderAssignmentService assignmentService;

    @Transactional
    public void handlePartnerOnLeave(DeliveryPartner partner) {

        List<Order> activeOrders =
                orderRepository.findByAssignedPartnerId(partner.getUser().getId());

        for (Order order : activeOrders) {

            order.setAssignedPartner(null);
            order.setStatus("waiting_for_partner");
            orderRepository.save(order);

            // Try immediate reassignment (excluding this partner)
          //  assignmentService.assignOrder(order.getId(), partner.getId());
        }
    }
}