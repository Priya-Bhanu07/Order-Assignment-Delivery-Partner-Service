package com.tarrina.orders.controller;
import com.tarrina.orders.dto.AssignOrderRequest;
import com.tarrina.orders.dto.AssignmentResultDto;
import com.tarrina.orders.dto.UnassignedOrderDto;
import com.tarrina.orders.entity.Order;
import com.tarrina.orders.repository.OrderRepository;
import com.tarrina.orders.service.OrderAssignmentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/assignments")
public class AssignmentController {

    private final OrderAssignmentService assignmentService;
    private final OrderRepository orderRepository;

    public AssignmentController(
            OrderAssignmentService assignmentService,
            OrderRepository orderRepository
    ) {
        this.assignmentService = assignmentService;
        this.orderRepository = orderRepository;
    }

    /**
     * POST /api/v1/assignments/assign
     * Auto-assign an unassigned order to best partner
     */
    @PostMapping("/assign")
    public AssignmentResultDto assign(
            @Valid @RequestBody AssignOrderRequest request
    ) {
        return assignmentService.assignOrder(request.getOrderId());
    }

    /**
     * POST /api/v1/assignments/reassign/{orderId}
     * Force reassignment of an order
    */
    @PostMapping("/reassign/{orderId}")

    public AssignmentResultDto reassign(
            @PathVariable Long orderId,
            @RequestParam String reason
    ) {
        return assignmentService.reassignOrder(orderId, reason);
    }

    /**
     * GET /api/v1/assignments/unassigned
     * List orders waiting for assignment with age
     */
    @GetMapping("/unassigned")
    public List<UnassignedOrderDto> unassignedOrders() {

        List<Order> orders = orderRepository.findActiveUnassignedOrders();

        return orders.stream()
                .map(order -> new UnassignedOrderDto(
                        order.getId(),
                        Duration.between(
                                order.getUpdatedAt(),
                                LocalDateTime.now()
                        ).toMinutes()
                ))
                .toList();
    }
}
