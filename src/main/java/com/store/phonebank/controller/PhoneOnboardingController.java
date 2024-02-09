package com.store.phonebank.controller;

import com.store.phonebank.services.seed.PhoneOnboardingSeeder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/phone-onboarding")
@Api(value = "Phone Onboarding", tags = {"Phone Onboarding"})
public class PhoneOnboardingController {

    private final PhoneOnboardingSeeder phoneOnboardingSeeder;

    public PhoneOnboardingController(PhoneOnboardingSeeder phoneOnboardingSeeder) {
        this.phoneOnboardingSeeder = phoneOnboardingSeeder;
    }

    @PostMapping("/seed")
    @ApiOperation(value = "load seed data of phones into the database")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully seeded the phone data"),
            @ApiResponse(code = 500, message = "Server error while seeding the phone data")
    })
    public Mono<ResponseEntity<Long>> seedPhoneData() {
        return phoneOnboardingSeeder.runSeed()
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(-1L));
    }
}