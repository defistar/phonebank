package com.store.phonebank.controller;

import com.store.phonebank.dto.PhoneAvailabilityResponseDto;
import com.store.phonebank.dto.PhoneBookingRequestDto;
import com.store.phonebank.dto.PhoneBookingResponseDto;
import com.store.phonebank.dto.PhoneReturnResponseDto;
import com.store.phonebank.services.booking.IPhoneBookingQueryService;
import com.store.phonebank.services.booking.IPhoneBookingService;
import com.store.phonebank.services.booking.IPhoneReturnService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/phone-booking")
@Tag(name = "Phone Booking", description = "Phone Booking API")
public class PhoneBookingController {

    private final IPhoneBookingService phoneBookingService;

    private final IPhoneReturnService phoneReturnService;

    private final IPhoneBookingQueryService phoneBookingQueryService;

    public PhoneBookingController(IPhoneBookingService phoneBookingService, IPhoneReturnService phoneReturnService, IPhoneBookingQueryService phoneBookingQueryService) {
        this.phoneBookingService = phoneBookingService;
        this.phoneReturnService = phoneReturnService;
        this.phoneBookingQueryService = phoneBookingQueryService;
    }

    @PostMapping("/book")
    @Operation(summary = "Book a phone")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully booked the phone"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<ResponseEntity<PhoneBookingResponseDto>> bookPhone(@RequestParam String brandName, @RequestParam String modelCode, @RequestParam String userName) {
        PhoneBookingRequestDto phoneBookingRequestDto = PhoneBookingRequestDto.builder()
                .brandName(brandName)
                .modelCode(modelCode)
                .userName(userName)
                .build();

        return phoneBookingService.bookPhone(phoneBookingRequestDto)
                .onErrorResume(e -> Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR)));
    }

    @PostMapping("/return")
    @Operation(summary = "Return a booked phone")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully returned a booked phone"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<ResponseEntity<PhoneReturnResponseDto>> returnPhone(@RequestParam String bookingId) {
        return phoneReturnService.returnBookedPhone(UUID.fromString(bookingId))
                .onErrorResume(e -> Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR)));
    }

    @GetMapping("/check-availability")
    @Operation(summary = "check availability for the phone")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "get availability of a phone by brand-name and model-code"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<ResponseEntity<PhoneAvailabilityResponseDto>> checkAvailability(@RequestParam String brandName, @RequestParam String modelCode) {
        return phoneBookingQueryService.checkPhoneAvailability(brandName, modelCode)
                .map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                .onErrorResume(e -> Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR)));
    }

}