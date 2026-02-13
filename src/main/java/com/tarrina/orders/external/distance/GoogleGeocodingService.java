package com.tarrina.orders.external.distance;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tarrina.orders.entity.GeocodedAddress;
import com.tarrina.orders.repository.GeocodedAddressRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;

@Service
public class GoogleGeocodingService {

    private final GoogleMapsClient client;
    private final GeocodedAddressRepository repo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GoogleGeocodingService(
            GoogleMapsClient client,
            GeocodedAddressRepository repo
    ) {
        this.client = client;
        this.repo = repo;
    }

    @Transactional
    public GeocodedAddress geocode(String address) {

        // 1️⃣ Cache hit
        return repo.findByAddress(address)
                .orElseGet(() -> fetchAndStore(address));
    }

    private GeocodedAddress fetchAndStore(String address) {

        String url = UriComponentsBuilder
                .fromHttpUrl("https://maps.googleapis.com/maps/api/geocode/json")
                .queryParam("address", address)
                .queryParam("key", client.getApiKey()) // ✅ DO NOT hardcode
                .toUriString();

        String response = client.get(url);

        try {
            JsonNode root = objectMapper.readTree(response);

            // ⚠️ Status OK but empty results
            JsonNode results = root.path("results");
            if (!results.isArray() || results.isEmpty()) {
                throw new IllegalStateException("Empty geocoding result for address: " + address);
            }

            JsonNode location = results.get(0)
                    .path("geometry")
                    .path("location");

            if (location.isMissingNode()) {
                throw new IllegalStateException("Invalid geocoding response structure");
            }

            BigDecimal lat = location.get("lat").decimalValue();
            BigDecimal lng = location.get("lng").decimalValue();

            GeocodedAddress geo = new GeocodedAddress();
            geo.setAddress(address);
            geo.setLatitude(lat);
            geo.setLongitude(lng);

            return repo.save(geo);

        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse geocoding response", e);
        }
    }
}
