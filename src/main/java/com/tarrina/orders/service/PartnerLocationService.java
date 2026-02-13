package com.tarrina.orders.service;

import com.tarrina.orders.entity.DeliveryPartner;
import com.tarrina.orders.entity.Location;
import com.tarrina.orders.entity.Locationable;
import com.tarrina.orders.entity.User;
import com.tarrina.orders.repository.LocationRepository;
import com.tarrina.orders.repository.LocationableRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PartnerLocationService {

    private final LocationRepository locationRepository;
    private final LocationableRepository locationableRepository;

    public PartnerLocationService(
            LocationRepository locationRepository,
            LocationableRepository locationableRepository
    ) {
        this.locationRepository = locationRepository;
        this.locationableRepository = locationableRepository;
    }

    @Transactional
    public void updateCurrentLocation(
            DeliveryPartner partner,
            BigDecimal lat,
            BigDecimal lng,
            User updatedBy
    ) {
        Location location = new Location();
        location.setLatitude(lat);
        location.setLongitude(lng);
        location.setCapturedAt(LocalDateTime.now());
        location.setSourceType("partner_gps");
        location.setCreatedAt(LocalDateTime.now());

        Location savedLocation = locationRepository.save(location);

        Locationable loc = new Locationable();
        loc.setLocation(savedLocation);
        loc.setLocationableType("PARTNER");
        loc.setLocationableId(partner.getId());
        loc.setPurpose("CURRENT_LOCATION");
        loc.setIsPrimary(true);
        loc.setAttachedBy(updatedBy);
        loc.setAttachedAt(LocalDateTime.now());
        loc.setVersion(1);
        loc.setCreatedAt(LocalDateTime.now());

        locationableRepository.save(loc);
    }
}
