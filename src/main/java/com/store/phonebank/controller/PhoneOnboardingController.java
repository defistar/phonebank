package com.store.phonebank.controller;

import com.store.phonebank.dto.PhoneDto;
import com.store.phonebank.services.PhoneOnboardingSeeder;
import com.store.phonebank.services.PhoneOnboardingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/phones")
@Api(value = "Phone Onboarding", tags = {"Phone Onboarding"})
public class PhoneOnboardingController {

    private final PhoneOnboardingService phoneOnboardingService;
    private final PhoneOnboardingSeeder phoneOnboardingSeeder;

    public PhoneOnboardingController(PhoneOnboardingService phoneOnboardingService, PhoneOnboardingSeeder phoneOnboardingSeeder) {
        this.phoneOnboardingService = phoneOnboardingService;
        this.phoneOnboardingSeeder = phoneOnboardingSeeder;
    }

    @PostMapping("/onboard")
    @ApiOperation(value = "Onboard a new phone")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully onboarded the phone"),
            @ApiResponse(code = 400, message = "Invalid PhoneDto supplied"),
            @ApiResponse(code = 500, message = "Server error while onboarding the phone")
    })
    public Mono<ResponseEntity<PhoneDto>> onboardPhone(@Valid @RequestBody PhoneDto phoneDto) {
        return phoneOnboardingService.savePhone(phoneDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ApiOperation(value = "Handle validation exceptions")
    public Mono<ResponseEntity<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining("\n"));
        return Mono.just(ResponseEntity.badRequest().body(errors));
    }
}