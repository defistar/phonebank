package com.store.phonebank;

import com.store.phonebank.services.query.PhoneInfoQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@Import({TestConfig.class})
public class PhoneInfoQueryServiceTest extends TestConfig {

    @Autowired
    private PhoneInfoQueryService phoneInfoQueryService;

    @Test
    public void testGetPhoneInfo() {
        StepVerifier.create(phoneInfoQueryService.getPhoneInfo("Samsung", "S9"))
                .assertNext(phoneDto -> {
                    assertEquals("Samsung", phoneDto.getBrandName());
                    assertEquals("S9", phoneDto.getModelCode());
                })
                .verifyComplete();
    }

    @Test
    public void testGetAllPhones() {
        StepVerifier.create(phoneInfoQueryService.getAllPhones())
                .expectNextCount(9)
                .verifyComplete();
    }
}