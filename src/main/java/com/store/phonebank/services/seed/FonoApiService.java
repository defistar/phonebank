package com.store.phonebank.services.seed;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.phonebank.dto.FonoDeviceInfoDto;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Service
public class FonoApiService {

    @Getter
    private final CircuitBreaker circuitBreaker;
    private final String fonoApiUrl;
    private final String fonoApiToken;
    @Setter
    private RestTemplate restTemplate;

    public FonoApiService(@Value("${fonoapi.url}") String fonoApiUrl,
                          @Value("${fonoapi.token}") String fonoApiToken) {
        this.fonoApiUrl = fonoApiUrl;
        this.fonoApiToken = fonoApiToken;
        this.restTemplate = new RestTemplate();

        // Create a custom configuration for a CircuitBreaker
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofMillis(1000))
                .permittedNumberOfCallsInHalfOpenState(2)
                .slidingWindowSize(2)
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .recordExceptions(RuntimeException.class)
                .build();

        // Create a CircuitBreaker using the custom configuration
        this.circuitBreaker = CircuitBreaker.of("fonoApiService", circuitBreakerConfig);
    }

    public FonoDeviceInfoDto getDeviceInfo(String brandName, String modelCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("brand", brandName);
        map.add("device", modelCode);
        map.add("token", fonoApiToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        return circuitBreaker.executeSupplier(() -> {
            ResponseEntity<String> response = restTemplate.exchange(fonoApiUrl, HttpMethod.POST, request, String.class);

            if (response.getStatusCodeValue() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    return mapper.readValue(response.getBody(), FonoDeviceInfoDto.class);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to parse JSON response", e);
                }
            } else {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusCode());
            }
        });
    }
}