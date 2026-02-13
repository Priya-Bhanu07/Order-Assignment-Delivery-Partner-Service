package com.tarrina.orders.external.distance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;


@Component
public class GoogleMapsClient {

    private final RestTemplate restTemplate;
    @Value("${google.maps.api-key}")
    private final String apiKey;

    public GoogleMapsClient(
            RestTemplateBuilder builder,
            @Value("${google.maps.api-key}") String apiKey
    ) {
        this.restTemplate = builder
                .connectTimeout(Duration.ofSeconds(2))
                .readTimeout(Duration.ofSeconds(3))
                .build();

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Google Maps API key missing");
        }

        this.apiKey = apiKey;
    }

    public String get(String url) {
        return restTemplate.getForObject(url, String.class);
    }

    // ✅ ADD THIS
    public String getApiKey() {
        return apiKey;
    }
}

