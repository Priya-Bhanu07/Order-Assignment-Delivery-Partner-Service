package com.tarrina.orders.service;

import com.tarrina.orders.dto.CreateOrderRequest;
import com.tarrina.orders.entity.*;
import com.tarrina.orders.repository.ContactRepository;
import com.tarrina.orders.repository.LocationRepository;
import com.tarrina.orders.repository.LocationableRepository;
import com.tarrina.orders.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ContactRepository contactRepository;
    private final LocationRepository locationRepository;
    private final LocationableRepository locationableRepository;

    public OrderService(
            OrderRepository orderRepository,
            ContactRepository contactRepository,
            LocationRepository locationRepository,
            LocationableRepository locationableRepository
    ) {
        this.orderRepository = orderRepository;
        this.contactRepository = contactRepository;
        this.locationRepository = locationRepository;
        this.locationableRepository = locationableRepository;
    }

    @Transactional
    public Order createOrder(CreateOrderRequest req, User user) {

        Contact contact = contactRepository.findById(req.contactId)
                .orElseThrow(() -> new EntityNotFoundException("Contact not found"));

        Order order = new Order();
        order.setContact(contact);
        order.setOrderItems(req.orderItems);
        order.setStatus("received");
        order.setPriority(req.priority);
        order.setDeliveryAddress(req.deliveryAddress);
        order.setDeliveryLatitude(req.deliveryLatitude);
        order.setDeliveryLongitude(req.deliveryLongitude);

        Order savedOrder = orderRepository.save(order);

        // Create Location
        Location location = new Location();
        location.setAddress(req.deliveryAddress);
        location.setLatitude(req.deliveryLatitude);
        location.setLongitude(req.deliveryLongitude);
        location.setCreatedAt(LocalDateTime.now());

        Location savedLocation = locationRepository.save(location);

        //Attach location to order
        attachDeliveryLocationToOrder(savedOrder, savedLocation, user);

        return savedOrder;
    }

    private void attachDeliveryLocationToOrder(
            Order order,
            Location location,
            User user
    ) {
        Locationable loc = new Locationable();
        loc.setLocation(location);
        loc.setLocationableType("ORDER");
        loc.setLocationableId(order.getId());
        loc.setPurpose("DELIVERY");
        loc.setIsPrimary(true);
        loc.setAttachedBy(user);
        loc.setAttachedAt(LocalDateTime.now());
        loc.setCreatedAt(LocalDateTime.now());

        locationableRepository.save(loc);
    }
}
