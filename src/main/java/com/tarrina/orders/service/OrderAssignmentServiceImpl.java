package com.tarrina.orders.service;

import com.tarrina.orders.dto.AssignmentResultDto;
import com.tarrina.orders.entity.DeliveryPartner;
import com.tarrina.orders.entity.Order;
import com.tarrina.orders.entity.PartnerAssignment;
import com.tarrina.orders.entity.User;
import com.tarrina.orders.external.distance.DistanceCalculator;
import com.tarrina.orders.external.distance.DistanceCalculatorSerive;
import com.tarrina.orders.repository.DeliveryPartnerRepository;
import com.tarrina.orders.repository.OrderRepository;
import com.tarrina.orders.repository.PartnerAssignmentRepository;
import com.tarrina.orders.repository.ServiceZoneRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class OrderAssignmentServiceImpl implements OrderAssignmentService {

    private final OrderRepository orderRepository;
    private final DeliveryPartnerRepository deliveryPartnerRepository;
    private final PartnerAssignmentRepository partnerAssignmentRepository;
    private final DistanceCalculatorSerive distanceCalculator;

    private final ServiceZoneRepository serviceZoneRepository;


    public OrderAssignmentServiceImpl(
            OrderRepository orderRepository,
            DeliveryPartnerRepository deliveryPartnerRepository,
            PartnerAssignmentRepository partnerAssignmentRepository,
            DistanceCalculatorSerive distanceCalculator,
            ServiceZoneRepository serviceZoneRepository
    ) {
        this.orderRepository = orderRepository;
        this.deliveryPartnerRepository = deliveryPartnerRepository;
        this.partnerAssignmentRepository = partnerAssignmentRepository;
        this.distanceCalculator = distanceCalculator;
        this.serviceZoneRepository=serviceZoneRepository;
    }

    /**
     *(ZONE + LOCKING + CAPACITY + DISTANCE)
     */
    @Override
    @Transactional
    public AssignmentResultDto assignOrder(Long orderId) {
        return assignOrder(orderId, null);
    }


    /**
     * assign with excluded partner (used for reassignment)
     */
   // @Transactional
    private AssignmentResultDto assignOrder(Long orderId,Long excludedPartnerId) {

        // 1️⃣ Lock order (prevent double assignment)
        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (order.getAssignedPartner() != null) {
            return new AssignmentResultDto(
                    order.getId(),
                    order.getAssignedPartner().getId(),
                    null,
                    null,
                    "ALREADY_ASSIGNED"
            );
        }

        if (order.getDeliveryLatitude() == null ||
                order.getDeliveryLongitude() == null) {

            order.setStatus("waiting_for_partner");
            return AssignmentResultDto.noPartner(orderId, "MISSING_COORDINATES");
        }

        double lat = order.getDeliveryLatitude().doubleValue();
        double lng = order.getDeliveryLongitude().doubleValue();

        //  Filter available partners
        List<DeliveryPartner> candidates =
                deliveryPartnerRepository.findAll()
                        .stream()
                        .filter(p -> "available".equalsIgnoreCase(p.getAvailabilityStatus()))
                        .filter(p -> p.getDeletedAt() == null)
                        .filter(p -> p.getCurrentActiveOrders() < p.getMaxOrdersPerDay())
                        .toList();

        if (candidates.isEmpty()) {
            order.setStatus("waiting_for_partner");
            return AssignmentResultDto.noPartner(orderId, "NO_AVAILABLE_PARTNERS");
        }

        // Service zone filter
        List<DeliveryPartner> zoneFiltered =
                candidates.stream()
                        .filter(p ->
                                serviceZoneRepository.isOrderInsidePartnerZone(
                                        p.getId(),
                                        lng,
                                        lat
                                )
                        )
                        .toList();

        if (zoneFiltered.isEmpty()) {
            order.setStatus("ZONE_GAP_REVIEW");
            return AssignmentResultDto.noPartner(orderId, "OUTSIDE_SERVICE_ZONE");
        }

        // Score partners
        DeliveryPartner bestPartner = null;
        BigDecimal bestScore = BigDecimal.ZERO;
        BigDecimal bestDistance = null;

        for (DeliveryPartner partner : zoneFiltered) {

            // Lock partner row
            DeliveryPartner locked =
                    deliveryPartnerRepository.findByIdForUpdate(partner.getId())
                            .orElse(null);

            if (locked == null) continue;

            if (locked.getCurrentActiveOrders() >= locked.getMaxOrdersPerDay())
                continue;

            if (locked.getCurrentLatitude() == null ||
                    locked.getCurrentLongitude() == null)
                continue;

            if (order.getDeliveryLatitude() == null ||
                    order.getDeliveryLongitude() == null) {

                throw new IllegalStateException(
                        "Order does not have delivery coordinates"
                );
            }

            BigDecimal distance;

            try {
                distance = distanceCalculator.calculateDistanceKm(
                        order.getDeliveryLatitude(),
                        order.getDeliveryLongitude(),
                        locked.getCurrentLatitude(),
                        locked.getCurrentLongitude()
                );
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
          BigDecimal score = calculateScore(distance, locked);

           if (score.compareTo(bestScore) > 0) {
                bestScore = score;
                bestPartner = locked;
                bestDistance = distance;
            }
        }

        if (bestPartner == null) {
            order.setStatus("waiting_for_partner");
            return AssignmentResultDto.noPartner(orderId, "NO_ELIGIBLE_PARTNER");
        }

        // Assign order
        order.setAssignedPartner(bestPartner);
        order.setStatus("assigned");
        order.setAssignedAt(LocalDateTime.now());

        // Update partner load
        bestPartner.setCurrentActiveOrders(
                bestPartner.getCurrentActiveOrders() + 1
        );

        deliveryPartnerRepository.save(bestPartner);
        orderRepository.save(order);

        // Audit
        PartnerAssignment audit = new PartnerAssignment();
        audit.setUuid(UUID.randomUUID());
        audit.setOrder(order);
        audit.setPartner(bestPartner);
        audit.setStatus("assigned");
        audit.setAssignedAt(LocalDateTime.now());
        audit.setDistanceToDelivery(bestDistance);
        audit.setScore(bestScore);
        audit.setAssignmentReason("AUTO_ASSIGNMENT");
        audit.setCreatedAt(LocalDateTime.now());
        audit.setUpdatedAt(LocalDateTime.now());

       partnerAssignmentRepository.save(audit);


        //  Return result
        return new AssignmentResultDto(
                order.getId(),
                bestPartner.getId(),   // NOT userId
                bestDistance,
                bestScore,
                "ASSIGNED_SUCCESSFULLY"
        );
    }
    /**
     * Reassignment  (partner unavailable / leave)
     */

     @Override
    @Transactional
    public AssignmentResultDto reassignOrder(Long orderId, String reason) {

        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

         DeliveryPartner previousPartnerUser = order.getAssignedPartner();
        if (previousPartnerUser == null) {
            throw new IllegalStateException("Order is not assigned");
        }

        DeliveryPartner previousPartner = deliveryPartnerRepository
                .findByUserId(previousPartnerUser.getId())
                .orElseThrow(() ->
                        new IllegalStateException("Previous user is not a delivery partner")
                );

        // Reduce load of previous partner
        previousPartner.setCurrentActiveOrders(
                Math.max(0, previousPartner.getCurrentActiveOrders() - 1)
        );
        deliveryPartnerRepository.save(previousPartner);

        // Unassign
        order.setAssignedPartner(null);
        order.setStatus("received");
        orderRepository.save(order);

        // Reassign excluding previous partner
        AssignmentResultDto result =
                assignOrder(orderId, previousPartner.getId());

        //Audit reassignment
        PartnerAssignment reassignment = new PartnerAssignment();
        reassignment.setUuid(UUID.randomUUID());
        reassignment.setOrder(order);
        reassignment.setPartner(
                deliveryPartnerRepository
                        .findByUserId(result.getPartnerUserId())
                        .orElseThrow()
        );
        reassignment.setPreviousPartner(previousPartner);
        reassignment.setStatus("reassigned");
        reassignment.setReassignedAt(LocalDateTime.now());
        reassignment.setReassignmentReason(reason);

        partnerAssignmentRepository.save(reassignment);

        return result;
    }

    /**
     * Scoring algorithm
     */
    private BigDecimal calculateScore(
            BigDecimal distanceKm,
            DeliveryPartner partner
    ) {
        BigDecimal loadFactor = BigDecimal.ONE.subtract(
                BigDecimal.valueOf(partner.getCurrentActiveOrders())
                        .divide(
                                BigDecimal.valueOf(partner.getMaxOrdersPerDay()),
                                MathContext.DECIMAL64
                        )
        );

        return BigDecimal.ONE
                .divide(distanceKm.add(BigDecimal.ONE), MathContext.DECIMAL64)
                .multiply(loadFactor);
    }

    @Override
    @Transactional
    public void handlePartnerOnLeave(DeliveryPartner partner) {
        List<Order> activeOrders =
                orderRepository.findByAssignedPartnerAndStatusIn(
                        partner.getUser(),
                        List.of("assigned", "dispatched")
                );

        for (Order order : activeOrders) {
            reassignOrder(order.getId(), "partner_on_leave");
        }

        // reset load AFTER reassignment attempts
        partner.setCurrentActiveOrders(0);
        deliveryPartnerRepository.save(partner);
    }

}
