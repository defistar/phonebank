package com.store.phonebank;

import com.store.phonebank.dto.PhoneDto;
import com.store.phonebank.repository.DeviceInfoRepository;
import com.store.phonebank.repository.PhoneRepository;
import com.store.phonebank.services.seed.DeviceInfoSeeder;
import com.store.phonebank.services.seed.PhoneOnboardingSeeder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Import({TestConfig.class, DeviceInfoSeeder.class, PhoneOnboardingSeeder.class, DeviceInfoRepository.class, PhoneRepository.class})
public class PhoneInfoQueryControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private DeviceInfoSeeder deviceInfoSeeder;

    @Autowired
    private PhoneRepository phoneRepository;

    @Autowired
    private PhoneOnboardingSeeder phoneOnboardingSeeder;

    @BeforeEach
    public void setup() {
        deviceInfoSeeder.runSeed().block();
        phoneOnboardingSeeder.runSeed().block();
    }

    @AfterEach
    public void cleanup() {
        phoneRepository.deleteAll().block();
    }

    @Test
    public void getPhoneInfoTest() {
        String brandName = "Samsung"; // Use a brand name that is inserted by the seeder
        String modelCode = "SM-A505F"; // Use a model code that is inserted by the seeder

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/phone-info/one")
                        .queryParam("brandName", brandName)
                        .queryParam("modelCode", modelCode)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(PhoneDto.class)
                .consumeWith(response -> {
                    PhoneDto phoneDto = response.getResponseBody();
                    // Add assertions for the fields of the phoneDto
                });
    }

    @Test
    public void getAllPhonesTest() {
        webTestClient.get()
                .uri("/api/v1/phone-info/all")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PhoneDto.class)
                .consumeWith(response -> {
                    List<PhoneDto> phoneDtos = response.getResponseBody();
                    // Add assertions for the phoneDtos list
                });
    }
}