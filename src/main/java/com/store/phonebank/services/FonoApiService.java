package com.store.phonebank.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.store.phonebank.dto.FonoDeviceInfoDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.beans.factory.annotation.Value;

@Service
public class FonoApiService {
    @Value("${fonoapi.url}")
    private String fonoApiUrl;

    @Value("${fonoapi.token}")
    private String fonoApiToken;

    public FonoDeviceInfoDto getDeviceInfo(String brandName, String modelCode) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("brand", brandName);
        map.add("device", modelCode);
        map.add("token", fonoApiToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

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
    }
}
