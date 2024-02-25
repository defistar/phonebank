package com.store.phonebank;


import com.store.phonebank.config.BookingStatus;
import com.store.phonebank.dto.PhoneBookingResponseDto;
import com.store.phonebank.dto.PhoneReturnResponseDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@SpringBootTest
@Profile("test")
@Import({TestConfig.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PhoneBookingControllerTest extends TestConfig {


    private WebTestClient webTestClient;

    @BeforeEach
    void setUp(@Autowired ApplicationContext context) {
        webTestClient = WebTestClient.bindToApplicationContext(context).configureClient().baseUrl("/api/v1").build();
    }

    @Test
    @Order(2)
    public void testPhoneBooking() {
        // Test the phone booking API
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/phone-booking/book")
                        .queryParam("brandName", "Samsung")
                        .queryParam("modelCode", "S9")
                        .queryParam("userName", "Christopher Nolan")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(PhoneBookingResponseDto.class)
                .consumeWith(response -> {
                    assert response.getResponseHeaders().get("Content-Type").get(0).equals("application/json");
                    PhoneBookingResponseDto responseBody = response.getResponseBody();
                    assert responseBody != null;
                    assert responseBody.getBrandName().equals("Samsung");
                    assert responseBody.getModelCode().equals("S9");
                    assert responseBody.getLastBookedUser().equals("Christopher Nolan");
                    assert !responseBody.getPhoneBookingId().toString().isEmpty();
                    assert responseBody.getAvailableCount() == 0;
                    assert responseBody.getPhoneCount() == 1;
                    assert responseBody.getBookingStatus().equals(BookingStatus.SUCCESSFUL);
                    assert responseBody.getLastBookedAt() != null;
                });

    }

    @Test
    @Order(1)
    public void testPhoneBookingAndReturn() {
        // Test the phone booking API
        AtomicReference<UUID> phoneBookingId = new AtomicReference<>();
        AtomicReference<LocalDateTime> lastBookedAt = new AtomicReference<>();
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/phone-booking/book")
                        .queryParam("brandName", "Samsung")
                        .queryParam("modelCode", "S9")
                        .queryParam("userName", "Christopher Nolan")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(PhoneBookingResponseDto.class)
                .consumeWith(response -> {
                    assert response.getResponseHeaders().get("Content-Type").get(0).equals("application/json");
                    PhoneBookingResponseDto responseBody = response.getResponseBody();
                    assert responseBody != null;
                    assert responseBody.getBrandName().equals("Samsung");
                    assert responseBody.getModelCode().equals("S9");
                    assert responseBody.getLastBookedUser().equals("Christopher Nolan");
                    assert !responseBody.getPhoneBookingId().toString().isEmpty();
                    System.out.println("PhoneBookingId is: " +responseBody.getPhoneBookingId());
                    phoneBookingId.set(responseBody.getPhoneBookingId());
                    assert responseBody.getAvailableCount() == 0;
                    assert responseBody.getPhoneCount() == 1;
                    assert responseBody.getBookingStatus().equals(BookingStatus.SUCCESSFUL);
                    assert responseBody.getLastBookedAt() != null;
                    lastBookedAt.set(responseBody.getLastBookedAt());
                });

        // Test the phone return API
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/phone-booking/return")
                        .queryParam("bookingId", phoneBookingId.get().toString())
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(PhoneReturnResponseDto.class)
                .consumeWith(response -> {
                    assert response.getResponseHeaders().get("Content-Type").get(0).equals("application/json");
                    PhoneReturnResponseDto responseBody = response.getResponseBody();
                    assert responseBody != null;
                    assert responseBody.getBrandName().equals("Samsung");
                    assert responseBody.getModelCode().equals("S9");
                    assert responseBody.isReturned();
                    assert responseBody.getAvailability().equals("Yes");
                    assert responseBody.getLastBookedUser().equals("Christopher Nolan");
                    assert responseBody.getPhoneBookingId().equals(phoneBookingId.get());
                    assert responseBody.getLastBookedAt() != null;
                    assert responseBody.getLastBookedAt().equals(lastBookedAt.get());
                });

    }

    @Test
    @Order(3)
    public void testPhoneBookingWithCircuitBreaker() throws InterruptedException {
        // Subsequent bookings fail due to non-availability of the phone
        for (int i = 0; i < 50; i++) {
            webTestClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/phone-booking/book")
                            .queryParam("brandName", "Samsung")
                            .queryParam("modelCode", "S9")
                            .queryParam("userName", "Christopher Nolan")
                            .build())
                    .exchange()
                    .expectStatus().is5xxServerError()
                    .expectBody(PhoneBookingResponseDto.class)
                    .consumeWith(response -> {
                        PhoneBookingResponseDto responseBody = response.getResponseBody();
                        assert responseBody != null;
                        assert responseBody.getBookingStatus().equals(BookingStatus.FAILED_PHONE_NOT_AVAILABLE);
                    });
        }

        Thread.sleep(3000);

        // After 50 failed requests and wait for 1 sec, the circuit breaker should be in half-open and further requests should be blocked
        for (int i = 0; i < 10; i++) {
            webTestClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/phone-booking/book")
                            .queryParam("brandName", "Samsung")
                            .queryParam("modelCode", "S9")
                            .queryParam("userName", "Christopher Nolan")
                            .build())
                    .exchange()
                    .expectStatus().is5xxServerError()
                    .expectBody(PhoneBookingResponseDto.class)
                    .consumeWith(response -> {
                        PhoneBookingResponseDto responseBody = response.getResponseBody();
                        assert responseBody != null;
                        assert responseBody.getBookingStatus().equals(BookingStatus.FAILED_PHONE_NOT_AVAILABLE);
                    });
        }

        Thread.sleep(1000);

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/phone-booking/book")
                        .queryParam("brandName", "Samsung")
                        .queryParam("modelCode", "S9")
                        .queryParam("userName", "Christopher Nolan")
                        .build())
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(PhoneBookingResponseDto.class)
                .consumeWith(response -> {
                    PhoneBookingResponseDto responseBody = response.getResponseBody();
                    assert responseBody != null;
                    assertEquals(BookingStatus.FAILED_DUE_TO_CIRCUIT_BREAKER, responseBody.getBookingStatus(), "The booking status is not as expected");
                });
    }
}
