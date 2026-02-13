package com.tarrina.orders.external.distance;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tarrina.orders.entity.DistanceCache;
import com.tarrina.orders.repository.DistanceCacheRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;

@Service
public class GoogleDistanceMatrixService {

    private final GoogleMapsClient client;
    private final DistanceCacheRepository cacheRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public GoogleDistanceMatrixService(
            GoogleMapsClient client,
            DistanceCacheRepository cacheRepo
    ) {
        this.client = client;
        this.cacheRepo = cacheRepo;
    }

    @Transactional
    public BigDecimal getDistanceKm(
            BigDecimal oLat,
            BigDecimal oLng,
            BigDecimal dLat,
            BigDecimal dLng
    ) {

        // 1️⃣ Cache lookup
        return cacheRepo
                .findByOriginLatAndOriginLngAndDestLatAndDestLng(
                        oLat, oLng, dLat, dLng
                )
                .map(DistanceCache::getDistanceKm)
                .orElseGet(() -> fetchAndStore(oLat, oLng, dLat, dLng));
    }

    private BigDecimal fetchAndStore(
            BigDecimal oLat,
            BigDecimal oLng,
            BigDecimal dLat,
            BigDecimal dLng
    ) {

        String url = UriComponentsBuilder
                .fromHttpUrl("https://maps.googleapis.com/maps/api/distancematrix/json")
                .queryParam("origins", oLat + "," + oLng)
                .queryParam("destinations", dLat + "," + dLng)
                .queryParam("key", client.getApiKey())
                .toUriString();

        try {
            String response = client.get(url);

            JsonNode root = objectMapper.readTree(response);

            JsonNode rows = root.path("rows");
            if (!rows.isArray() || rows.isEmpty()) {
                throw new IllegalStateException("Empty distance matrix response");
            }

            JsonNode elements = rows.get(0).path("elements");
            if (!elements.isArray() || elements.isEmpty()) {
                throw new IllegalStateException("No distance elements returned");
            }

            JsonNode element = elements.get(0);

            if (!"OK".equals(element.path("status").asText())) {
                throw new IllegalStateException("Distance calculation failed");
            }

            long meters = element
                    .path("distance")
                    .path("value")
                    .asLong();

            BigDecimal distanceKm =
                    BigDecimal.valueOf(meters)
                            .divide(BigDecimal.valueOf(1000));

            // 2️⃣ Cache result
            DistanceCache cache = new DistanceCache();
            cache.setOriginLat(oLat);
            cache.setOriginLng(oLng);
            cache.setDestLat(dLat);
            cache.setDestLng(dLng);
            cache.setDistanceKm(distanceKm);

            cacheRepo.save(cache);

            return distanceKm;

        } catch (HttpClientErrorException.TooManyRequests ex) {
            // 429
            throw new IllegalStateException("Rate limited by Google Maps", ex);

        } catch (ResourceAccessException ex) {
            // timeout
            throw new IllegalStateException("Google Maps timeout", ex);

        } catch (Exception ex) {
            throw new IllegalStateException("Failed to fetch distance from Google Maps", ex);
        }
    }
}
