package com.tarrina.orders.service;

import com.tarrina.orders.dto.AssignmentResultDto;
import com.tarrina.orders.entity.DeliveryPartner;
import com.tarrina.orders.entity.Order;
import com.tarrina.orders.entity.User;
import com.tarrina.orders.external.distance.DistanceCalculatorSerive;
import com.tarrina.orders.repository.DeliveryPartnerRepository;
import com.tarrina.orders.repository.OrderRepository;
import com.tarrina.orders.repository.PartnerAssignmentRepository;
import com.tarrina.orders.repository.ServiceZoneRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderAssignmentServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private DeliveryPartnerRepository deliveryPartnerRepository;

    @Mock
    private PartnerAssignmentRepository partnerAssignmentRepository;

    @Mock
    private DistanceCalculatorSerive distanceCalculator;

    @Mock
    private ServiceZoneRepository serviceZoneRepository;

    @InjectMocks
    private OrderAssignmentServiceImpl service;

    @Test
    void shouldReturnAlreadyAssigned() {

        Order order = new Order();
        order.setId(1L);

        DeliveryPartner partner = new DeliveryPartner();
        partner.setId(100L);

        order.setAssignedPartner(partner);

        when(orderRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(order));

        AssignmentResultDto result = service.assignOrder(1L);

        assertEquals("ALREADY_ASSIGNED", result.getReason());
        assertEquals(100L, result.getPartnerUserId());
    }

    @Test
    void shouldAssignSuccessfully() {

        Order order = new Order();
        order.setId(2L);
        order.setAssignedPartner(null);
        order.setDeliveryLatitude(BigDecimal.valueOf(12.9));
        order.setDeliveryLongitude(BigDecimal.valueOf(77.5));

        DeliveryPartner partner = new DeliveryPartner();
        partner.setId(200L);
        partner.setAvailabilityStatus("available");
        partner.setDeletedAt(null);
        partner.setCurrentActiveOrders(0);
        partner.setMaxOrdersPerDay(5);
        partner.setCurrentLatitude(BigDecimal.valueOf(12.91));
        partner.setCurrentLongitude(BigDecimal.valueOf(77.51));

        when(orderRepository.findByIdForUpdate(2L))
                .thenReturn(Optional.of(order));

        when(deliveryPartnerRepository.findAll())
                .thenReturn(List.of(partner));

        when(serviceZoneRepository.isOrderInsidePartnerZone(anyLong(), anyDouble(), anyDouble()))
                .thenReturn(true);

        when(deliveryPartnerRepository.findByIdForUpdate(200L))
                .thenReturn(Optional.of(partner));

        when(distanceCalculator.calculateDistanceKm(any(), any(), any(), any()))
                .thenReturn(BigDecimal.valueOf(5));

        AssignmentResultDto result = service.assignOrder(2L);

        assertEquals("ASSIGNED_SUCCESSFULLY", result.getReason());
        assertEquals(200L, result.getPartnerUserId());
    }

    @Test
    void shouldReturnNoAvailablePartnersWhenAllFull() {

        Order order = new Order();
        order.setId(3L);
        order.setDeliveryLatitude(BigDecimal.valueOf(12.9));
        order.setDeliveryLongitude(BigDecimal.valueOf(77.5));

        DeliveryPartner fullPartner = new DeliveryPartner();
        fullPartner.setId(300L);
        fullPartner.setAvailabilityStatus("available");
        fullPartner.setDeletedAt(null);
        fullPartner.setCurrentActiveOrders(5);
        fullPartner.setMaxOrdersPerDay(5); // FULL

        when(orderRepository.findByIdForUpdate(3L))
                .thenReturn(Optional.of(order));

        when(deliveryPartnerRepository.findAll())
                .thenReturn(List.of(fullPartner));

        AssignmentResultDto result = service.assignOrder(3L);

        assertEquals("NO_AVAILABLE_PARTNERS", result.getReason());
    }
    @Test
    void shouldReturnOutsideServiceZone() {

        Order order = new Order();
        order.setId(4L);
        order.setDeliveryLatitude(BigDecimal.valueOf(12.9));
        order.setDeliveryLongitude(BigDecimal.valueOf(77.5));

        DeliveryPartner partner = new DeliveryPartner();
        partner.setId(400L);
        partner.setAvailabilityStatus("available");
        partner.setDeletedAt(null);
        partner.setCurrentActiveOrders(0);
        partner.setMaxOrdersPerDay(5);

        when(orderRepository.findByIdForUpdate(4L))
                .thenReturn(Optional.of(order));

        when(deliveryPartnerRepository.findAll())
                .thenReturn(List.of(partner));

        when(serviceZoneRepository.isOrderInsidePartnerZone(anyLong(), anyDouble(), anyDouble()))
                .thenReturn(false);

        AssignmentResultDto result = service.assignOrder(4L);

        assertEquals("OUTSIDE_SERVICE_ZONE", result.getReason());
    }
    @Test
    void shouldReturnMissingCoordinates() {

        Order order = new Order();
        order.setId(5L);
        order.setDeliveryLatitude(null);
        order.setDeliveryLongitude(null);

        when(orderRepository.findByIdForUpdate(5L))
                .thenReturn(Optional.of(order));

        AssignmentResultDto result = service.assignOrder(5L);

        assertEquals("MISSING_COORDINATES", result.getReason());
    }
    @Test
    void shouldThrowExceptionWhenDistanceFails() {

        Order order = new Order();
        order.setId(6L);
        order.setDeliveryLatitude(BigDecimal.valueOf(12.9));
        order.setDeliveryLongitude(BigDecimal.valueOf(77.5));

        DeliveryPartner partner = new DeliveryPartner();
        partner.setId(600L);
        partner.setAvailabilityStatus("available");
        partner.setDeletedAt(null);
        partner.setCurrentActiveOrders(0);
        partner.setMaxOrdersPerDay(5);
        partner.setCurrentLatitude(BigDecimal.valueOf(12.91));
        partner.setCurrentLongitude(BigDecimal.valueOf(77.51));

        when(orderRepository.findByIdForUpdate(6L))
                .thenReturn(Optional.of(order));

        when(deliveryPartnerRepository.findAll())
                .thenReturn(List.of(partner));

        when(serviceZoneRepository.isOrderInsidePartnerZone(anyLong(), anyDouble(), anyDouble()))
                .thenReturn(true);

        when(deliveryPartnerRepository.findByIdForUpdate(600L))
                .thenReturn(Optional.of(partner));

        when(distanceCalculator.calculateDistanceKm(any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Distance API failure"));

        try {
            service.assignOrder(6L);
        } catch (RuntimeException ex) {
            assertEquals("Distance API failure", ex.getMessage());
        }
    }
    @Test
    void shouldNotReassignIfAlreadyAssigned() {

        Order order = new Order();
        order.setId(7L);

        DeliveryPartner partner = new DeliveryPartner();
        partner.setId(700L);

        order.setAssignedPartner(partner);

        when(orderRepository.findByIdForUpdate(7L))
                .thenReturn(Optional.of(order));

        AssignmentResultDto result = service.assignOrder(7L);

        assertEquals("ALREADY_ASSIGNED", result.getReason());
    }
}