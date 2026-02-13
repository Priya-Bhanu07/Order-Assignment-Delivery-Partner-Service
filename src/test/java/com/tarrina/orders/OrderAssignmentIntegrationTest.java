package com.tarrina.orders;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tarrina.orders.entity.*;
import com.tarrina.orders.repository.*;
import com.tarrina.orders.service.OrderAssignmentService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OrderAssignmentIntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DeliveryPartnerRepository partnerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderAssignmentService assignmentService;
    @Autowired
    private  ContactRepository contactRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void fullAssignmentFlowShouldPersistCorrectly() {

        //  Create user
        User user = new User();
        user.setUuid(UUID.randomUUID());
        user.setName("Test Partner");
        user.setPassword("test123");
        user.setEmail("test@example.com");
        user.setStatus("active");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);

        //  Create delivery partner
        DeliveryPartner partner = new DeliveryPartner();
        partner.setUser(user);
        partner.setAvailabilityStatus("available");
        partner.setMaxOrdersPerDay(5);
        partner.setCurrentActiveOrders(0);
        partner.setCurrentLatitude(BigDecimal.valueOf(12.91));
        partner.setCurrentLongitude(BigDecimal.valueOf(77.51));
        partner.setCreatedAt(LocalDateTime.now());
        partner.setUpdatedAt(LocalDateTime.now());
        partnerRepository.save(partner);

        Contact contact = new Contact();
        contact.setName("Test Customer");
        contact.setEmail("priya22gmail.com");
        contact.setPhone("9999999999");
        contactRepository.save(contact);
        // Create order
        Order order = new Order();

        order.setUuid(UUID.randomUUID());
        order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0,8));

        order.setContact(contact);     // REQUIRED FK

        order.setStatus("received");
        order.setPriority("normal");

        order.setDeliveryAddress("MG Road, Bangalore");
        order.setDeliveryLatitude(BigDecimal.valueOf(12.90));
        order.setDeliveryLongitude(BigDecimal.valueOf(77.50));

        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setOrderItems(new ObjectMapper().createArrayNode());
        orderRepository.save(order);

        //  Call real service
        assignmentService.assignOrder(order.getId());

        entityManager.flush();
        entityManager.clear();

//  Reload from DB
        Order saved = orderRepository.findById(order.getId()).orElseThrow();

// Assertions
        DeliveryPartner bestPartner =new DeliveryPartner();
        assertNotNull(saved.getAssignedPartner());
        assertEquals("assigned", saved.getStatus());
        DeliveryPartner updatedPartner =
                partnerRepository.findById(partner.getId()).orElseThrow();
    }
}