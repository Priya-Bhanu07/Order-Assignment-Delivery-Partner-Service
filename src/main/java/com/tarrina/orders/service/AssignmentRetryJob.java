package com.tarrina.orders.service;
import com.tarrina.orders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AssignmentRetryJob {

    private final OrderRepository orderRepository;
    private final OrderAssignmentService assignmentService;

    // Runs every 5 minutes
    @Scheduled(fixedRate = 300_000)
    @Transactional
    public void retryUnassignedOrders() {

        orderRepository.findByStatusIn(
                List.of("waiting_for_partner", "ZONE_GAP_REVIEW")
        ).forEach(order -> {

            try {
                assignmentService.assignOrder(order.getId());
            } catch (Exception e) {
                // log but continue
                System.err.println(
                        "Retry failed for order " + order.getId() + ": " + e.getMessage()
                );
            }
        });
    }
}