package com.store.phonebank.controller;

import com.store.phonebank.services.seed.PhoneOnboardingSeeder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/v1/phone-onboarding")
@Tag(name = "Phone Onboarding", description = "Phone Onboarding API")
public class PhoneOnboardingController {

    private final PhoneOnboardingSeeder phoneOnboardingSeeder;

    public PhoneOnboardingController(PhoneOnboardingSeeder phoneOnboardingSeeder) {
        this.phoneOnboardingSeeder = phoneOnboardingSeeder;
    }

    @PostMapping("/seed")
    @Operation(summary = "load seed data of phones into the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully seeded the phone data"),
            @ApiResponse(responseCode = "500", description = "Server error while seeding the phone data")
    })
    public Mono<ResponseEntity<Long>> seedPhoneData() {
        return phoneOnboardingSeeder.runSeed()
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(-1L));
    }
}