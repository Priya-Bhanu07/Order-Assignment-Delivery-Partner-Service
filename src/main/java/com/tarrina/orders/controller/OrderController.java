package com.tarrina.orders.controller;

import com.tarrina.orders.dto.*;
import com.tarrina.orders.entity.Contact;
import com.tarrina.orders.entity.Order;
import com.tarrina.orders.entity.User;
import com.tarrina.orders.repository.ContactRepository;
import com.tarrina.orders.repository.OrderRepository;
import com.tarrina.orders.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final ContactRepository contactRepository;
    private final OrderService orderService;



    public OrderController(OrderRepository orderRepository,ContactRepository contactRepository,
                           OrderService orderService) {
        this.orderRepository = orderRepository;
        this.contactRepository= contactRepository;
        this.orderService=orderService;
    }

    /*CREATE ORDER*/

    @PostMapping
    public OrderResponse createOrder(@RequestBody @Valid CreateOrderRequest req) {

        User systemUser = null;
        Order saved = orderService.createOrder(req, systemUser);
        OrderResponse res = new OrderResponse();
        res.id = saved.getId();
        res.uuid = saved.getUuid();
        res.status = saved.getStatus();
        res.assignedPartnerUserId =
                saved.getAssignedPartner() != null
                        ? saved.getAssignedPartner().getId()
                        : null;

        return res;
    }

    /*GET ORDER*/

    @GetMapping("/{orderId}")
    public OrderDetailsResponse getOrder(@PathVariable Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        return OrderDetailsResponse.from(order);
    }

    /*LIST ORDERS */

    @GetMapping
    public Page<OrderListItemResponse> listOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long contactId,
            @RequestParam(required = false) Long partnerId,
            Pageable pageable
    ) {
        Page<Order> orders = orderRepository.findByFilters(
                status, contactId, partnerId, pageable
        );

        return orders.map(OrderListItemResponse::from);
    }


    /*UPDATE STATUS */

    @PatchMapping("/{orderId}/status")
    public OrderStatusResponse updateStatus(
            @PathVariable Long orderId,
            @RequestParam String status
    ) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        String previous = order.getStatus();

        validateStatusTransition(previous, status);

        // timestamps based on NEXT status
        if ("assigned".equals(status)) {
            order.setAssignedAt(LocalDateTime.now());
        }
        if ("dispatched".equals(status)) {
            order.setDispatchedAt(LocalDateTime.now());
        }
        if ("delivered".equals(status)) {
            order.setDeliveredAt(LocalDateTime.now());
        }

        order.setStatus(status);

        orderRepository.save(order);

        return new OrderStatusResponse(
                order.getId(),
                previous,
                status,
                LocalDateTime.now().toString()
        );
    }


    /*STATUS VALIDATION*/

    private void validateStatusTransition(String current, String next) {

        Map<String, List<String>> allowed = Map.of(
                "received", List.of("assigned"),
                "assigned", List.of("dispatched"),
                "dispatched", List.of("delivered")
        );

        if (current.equals("delivered")) {
            throw new IllegalStateException("Delivered order cannot change status");
        }

        if (!allowed.getOrDefault(current, List.of()).contains(next)) {
            throw new IllegalStateException(
                    "Invalid status transition: " + current + " → " + next
            );
        }
    }
}
