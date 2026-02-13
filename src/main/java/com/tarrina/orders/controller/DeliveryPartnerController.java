package com.tarrina.orders.controller;

import com.tarrina.orders.dto.CreateDeliveryPartnerRequest;
import com.tarrina.orders.dto.DeliveryPartnerResponse;
import com.tarrina.orders.dto.UpdateAvailabilityRequest;
import com.tarrina.orders.entity.*;
import com.tarrina.orders.external.distance.HaversineUtil;
import com.tarrina.orders.repository.*;
import com.tarrina.orders.service.OrderAssignmentService;
import com.tarrina.orders.service.PartnerLocationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.locationtech.jts.geom.*;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/partners")
public class DeliveryPartnerController {

    private final DeliveryPartnerRepository partnerRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderAssignmentService assignmentService;
    private final AvailabilityLogRepository availabilityLogRepository;
    private final PartnerLocationService partnerLocationService;
    private final HaversineUtil haversineUtil;

    private final ServiceZoneRepository serviceZoneRepository;


    public DeliveryPartnerController(
            DeliveryPartnerRepository partnerRepository,
            UserRepository userRepository,
            OrderRepository orderRepository,
            OrderAssignmentService assignmentService,
            AvailabilityLogRepository availabilityLogRepository,
            PartnerLocationService  partnerLocationService,
            HaversineUtil haversineUtil,
            ServiceZoneRepository serviceZoneRepository
    ) {
        this.partnerRepository = partnerRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.assignmentService = assignmentService;
        this.availabilityLogRepository = availabilityLogRepository;
        this.partnerLocationService=partnerLocationService;
        this.haversineUtil=haversineUtil;
        this.serviceZoneRepository=serviceZoneRepository;
    }

    /* REGISTER PARTNER */

    @PostMapping
    @Transactional
    public DeliveryPartnerResponse register(
            @RequestBody @Valid CreateDeliveryPartnerRequest req
    ) {

        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (partnerRepository.findByUserId(user.getId()).isPresent()) {
            throw new IllegalStateException(

                    "This user is already registered as a delivery partner"
            );
        }

        DeliveryPartner partner = new DeliveryPartner();
        partner.setUser(user);
        partner.setVehicleType(req.getVehicleType());
        partner.setVehicleNumber(req.getVehicleNumber());
        partner.setVehicleRegistration(String.valueOf(req.getVehicleRegistration()));
        partner.setMaxOrdersPerDay(req.getMaxOrdersPerDay());
        partner.setCurrentActiveOrders(0);
        partner.setAvailabilityStatus("available");
        partner.setLastStatusUpdate(LocalDateTime.now());
        partner.setCreatedAt(LocalDateTime.now());
        partner.setUpdatedAt(LocalDateTime.now());
        if (req.getServiceZoneId() != null) {
            ServiceZone zone =
                    serviceZoneRepository.findById(req.getServiceZoneId())
                            .orElseThrow(() ->
                                    new EntityNotFoundException("Service zone not found"));

            partner.setServiceZone(zone);
        }
        partner.setMetadata(req.getMetadata());

        //set lat/long on partner itself
        partner.setCurrentLatitude(req.getCurrentLatitude());
        partner.setCurrentLongitude(req.getCurrentLongitude());
        DeliveryPartner savedPartner = partnerRepository.save(partner);

        //Delegate location logic to service (for Location + Locationable tables)
        if (req.getCurrentLatitude() != null && req.getCurrentLongitude() != null) {
            partnerLocationService.updateCurrentLocation(
                    savedPartner,
                    req.getCurrentLatitude(),
                    req.getCurrentLongitude(),
                    user
            );
        }

        //Reload partner so location changes are visible
        DeliveryPartner refreshedPartner =
                partnerRepository.findById(savedPartner.getId()).orElseThrow();

        return toResponse(refreshedPartner);
    }



    /*LIST PARTNERS*/

    @GetMapping
    public List<DeliveryPartnerResponse> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String zone,
            @RequestParam(required = false) BigDecimal lat,
            @RequestParam(required = false) BigDecimal lng,
            @RequestParam(required = false, defaultValue = "5") BigDecimal radiusKm
    ) {

        List<DeliveryPartner> partners = partnerRepository.findAll();

        //Filter by STATUS
        if (status != null) {
            partners = partners.stream()
                    .filter(p ->
                            p.getAvailabilityStatus() != null &&
                                    p.getAvailabilityStatus().equalsIgnoreCase(status)
                    )
                    .toList();
        }

        //Filter by REAL SERVICE ZONE (correct way)
        if (zone != null) {
            partners = partners.stream()
                    .filter(p ->
                            p.getServiceZone() != null &&
                                    p.getServiceZone().getZoneName()
                                            .equalsIgnoreCase(zone)
                    )
                    .toList();
        }

        //Filter by DISTANCE (Haversine)
        if (lat != null && lng != null) {

            partners = partners.stream()
                    .filter(p ->
                            p.getCurrentLatitude() != null &&
                                    p.getCurrentLongitude() != null
                    )
                    .filter(p ->
                            haversineUtil.calculate(
                                    lat, lng,
                                    p.getCurrentLatitude(),
                                    p.getCurrentLongitude()
                            ).compareTo(radiusKm) <= 0
                    )
                    .toList();
        }

        return partners.stream()
                .map(this::toResponse)
                .toList();
    }


    /*GET PARTNER*/

    @GetMapping("/{partnerId}")
    public DeliveryPartnerResponse get(@PathVariable Long partnerId) {
        DeliveryPartner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new EntityNotFoundException("Partner not found"));
        return toResponse(partner);
    }

    /*UPDATE AVAILABILITY */

    @PatchMapping("/{partnerId}/availability")
    @Transactional
    public DeliveryPartnerResponse updateAvailability(
            @PathVariable Long partnerId,
            @RequestBody @Valid UpdateAvailabilityRequest req
    ) {

        DeliveryPartner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new EntityNotFoundException("Partner not found"));

        String previous = partner.getAvailabilityStatus();

        partner.setAvailabilityStatus(req.status);
        partner.setLastStatusUpdate(LocalDateTime.now());
        partner.setOnLeaveUntil(req.onLeaveUntil);
        partner.setLeaveReason(req.leaveReason);

        partnerRepository.save(partner);

        /*Availability Audit Log */
        PartnerAvailabilityLog log = new PartnerAvailabilityLog();
        log.setPartner(partner);
        log.setPreviousStatus(previous);
        log.setNewStatus(req.status);
        log.setChangeReason(
                req.leaveReason != null ? req.leaveReason : "manual_update"
        );
        log.setTriggeredBy("api");
        log.setCreatedAt(LocalDateTime.now());

        availabilityLogRepository.save(log);

        /*Reassign ONLY active orders */
        if (!"on_leave".equalsIgnoreCase(previous)
                && "on_leave".equalsIgnoreCase(req.status)) {

            assignmentService.handlePartnerOnLeave(partner);
        }

        return toResponse(partner);
    }


    /*PARTNER ORDERS */

    @GetMapping("/{partnerId}/orders")
    public List<Long> getAssignedOrders(@PathVariable Long partnerId) {

        DeliveryPartner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new EntityNotFoundException("Partner not found"));

        Long userId = partner.getUser().getId();

        return orderRepository
                .findActiveOrdersByPartnerUserId(userId)
                .stream()
                .map(Order::getId)
                .toList();
    }
 /* MAPPER*/

    private DeliveryPartnerResponse toResponse(DeliveryPartner p) {

        DeliveryPartnerResponse r = new DeliveryPartnerResponse();

        r.setId(p.getId());
        r.setUserId(p.getUser().getId());
        r.setUserName(p.getUser().getName());

        r.setVehicleType(p.getVehicleType());
        r.setVehicleNumber(p.getVehicleNumber());
        r.setVehicleRegistration(p.getVehicleRegistration());

        r.setAvailabilityStatus(p.getAvailabilityStatus());
        r.setCurrentActiveOrders(p.getCurrentActiveOrders());
        r.setMaxOrdersPerDay(p.getMaxOrdersPerDay());
        r.setLastStatusUpdate(p.getLastStatusUpdate());

        // ====== FIX: Convert real ServiceZone → JsonNode ======
        if (p.getServiceZone() != null) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper =
                        new com.fasterxml.jackson.databind.ObjectMapper();

                r.setServiceZones(
                        mapper.readTree(
                                """
                                {
                                  "zones": [
                                    {"zone": "%s"}
                                  ]
                                }
                                """.formatted(p.getServiceZone().getZoneName())
                        )
                );
            } catch (Exception e) {
                // fallback (should rarely happen)
                r.setServiceZones(null);
            }
        }
        r.setMetadata(p.getMetadata());


        // Location
        r.setCurrentLatitude(p.getCurrentLatitude());
        r.setCurrentLongitude(p.getCurrentLongitude());
        r.setLocationUpdatedAt(p.getLocationUpdatedAt());

        // Metrics
        r.setTotalDeliveries(p.getTotalDeliveries());
        r.setSuccessfulDeliveries(p.getSuccessfulDeliveries());
        r.setFailedDeliveries(p.getFailedDeliveries());
        r.setAverageDeliveryTimeMinutes(p.getAverageDeliveryTimeMinutes());
        r.setRating(p.getRating());

        // Leave
        r.setOnLeaveUntil(p.getOnLeaveUntil());
        r.setLeaveReason(p.getLeaveReason());

        // Audit
        r.setCreatedAt(p.getCreatedAt());
        r.setUpdatedAt(p.getUpdatedAt());

        return r;
    }

    @PatchMapping("/{partnerId}/zone/{zoneId}")
    public DeliveryPartnerResponse assignZone(
            @PathVariable Long partnerId,
            @PathVariable Long zoneId) {

        DeliveryPartner partner =
                partnerRepository.findById(partnerId).orElseThrow();

        ServiceZone zone =
                serviceZoneRepository.findById(zoneId).orElseThrow();

        partner.setServiceZone(zone);

        DeliveryPartner saved = partnerRepository.save(partner);

        return toResponse(saved);
    }

}
