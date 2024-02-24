package com.store.phonebank;

import com.store.phonebank.dto.PhoneDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@Import({TestConfig.class})
public class PhoneInfoQueryControllerTest extends TestConfig {

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp(@Autowired ApplicationContext context) {
        webTestClient = WebTestClient.bindToApplicationContext(context).configureClient().baseUrl("/api/v1").build();
    }

    @Test
    public void getPhoneInfoTest() {
        String brandName = "Samsung"; // Use a brand name that is inserted by the seeder
        String modelCode = "S9"; // Use a model code that is inserted by the seeder

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/phone-info/one")
                        .queryParam("brandName", brandName)
                        .queryParam("modelCode", modelCode)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(PhoneDto.class)
                .consumeWith(response -> {
                    PhoneDto phoneDto = response.getResponseBody();
                    assert phoneDto != null;
                    assert phoneDto.getBrandName().equals(brandName);
                    assert phoneDto.getModelCode().equals(modelCode);
                    assert phoneDto.getPhoneCount() == 1;
                    assert phoneDto.getAvailableCount() == 1;
                    assert phoneDto.getDeviceInfo() != null;
                    assert phoneDto.getDeviceInfo().getBrandName().equals(brandName);
                    assert phoneDto.getDeviceInfo().getModelCode().equals(modelCode);
                    assert phoneDto.getDeviceInfo().getTechnology().equals("GSM,CDMA,HSPA,EVDO,LTE");
                    assert phoneDto.getDeviceInfo().get_2g_bands().equals("GSM 850,GSM 900,GSM 1800,GSM 1900");
                    assert phoneDto.getDeviceInfo().get_3g_bands().equals("HSDPA 850, HSDPA 900, HSDPA 1700(AWS), HSDPA 1900, HSDPA 2100");
                    assert phoneDto.getDeviceInfo().get_4g_bands().equals("LTE band 1(2100), LTE band 2(1900), LTE band 3(1800), LTE band 4(1700/2100), LTE band 5(850), LTE band 7(2600), LTE band 8(900), LTE band 12(700), LTE band 13(700), LTE band 17(700), LTE band 18(800), LTE band 19(800), LTE band 20(800), LTE band 25(1900), LTE band 26(850), LTE band 28(700), LTE band 32(1500), LTE band 66(1700/2100), LTE band 38(2600), LTE band 39(1900), LTE band 40(2300), LTE band 41(2500)");
                });
    }

    @Test
    public void getAllPhonesTest() {
        webTestClient.get()
                .uri("/phone-info/all")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PhoneDto.class)
                .consumeWith(response -> {
                    List<PhoneDto> phoneDtos = response.getResponseBody();
                    assert phoneDtos != null;
                    assert !phoneDtos.isEmpty();
                    assert phoneDtos.get(0).getBrandName().equals("Samsung");
                });
    }
}