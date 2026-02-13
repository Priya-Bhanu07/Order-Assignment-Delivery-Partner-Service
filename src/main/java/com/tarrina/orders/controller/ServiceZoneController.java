package com.tarrina.orders.controller;

import com.tarrina.orders.dto.CreateServiceZoneRequest;
import com.tarrina.orders.dto.ServiceZoneResponse;
import com.tarrina.orders.entity.ServiceZone;
import com.tarrina.orders.repository.ServiceZoneRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/service-zones")
@RequiredArgsConstructor
public class ServiceZoneController {

    private final ServiceZoneRepository serviceZoneRepository;

    @PostMapping
    public ServiceZoneResponse createZone(
            @RequestBody CreateServiceZoneRequest req
    ) throws Exception {

        Polygon polygon = (Polygon) new WKTReader()
                .read(req.getWktPolygon());

        ServiceZone zone = new ServiceZone();
        zone.setUuid(UUID.randomUUID());
        zone.setZoneName(req.getZoneName());
        zone.setBoundary(polygon);
        zone.setCreatedAt(LocalDateTime.now());
        zone.setUpdatedAt(LocalDateTime.now());

        ServiceZone saved = serviceZoneRepository.save(zone);

        ServiceZoneResponse res = new ServiceZoneResponse();
        res.setId(saved.getId());
        res.setUuid(saved.getUuid());
        res.setZoneName(saved.getZoneName());
        res.setBoundaryWkt(saved.getBoundary().toText());

        return res;
    }
}