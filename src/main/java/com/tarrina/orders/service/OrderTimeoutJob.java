package com.tarrina.orders.service;

import com.tarrina.orders.entity.Order;
import com.tarrina.orders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderTimeoutJob {

    private final OrderRepository orderRepository;

    @Scheduled(fixedRate = 300_000) // every 5 min
    public void checkTimeoutOrders() {

        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(30);

        List<Order> timedOut =
                orderRepository.findByStatusAndCreatedAtBefore(
                        "waiting_for_partner", cutoff
                );

        for (Order order : timedOut) {
            order.setStatus("ESCALATED_HUMAN_REVIEW");
            orderRepository.save(order);
        }
    }
}