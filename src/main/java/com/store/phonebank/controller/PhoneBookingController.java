package com.store.phonebank.controller;

import com.store.phonebank.dto.PhoneAvailabilityResponseDto;
import com.store.phonebank.dto.PhoneBookingRequestDto;
import com.store.phonebank.dto.PhoneBookingResponseDto;
import com.store.phonebank.dto.PhoneReturnResponseDto;
import com.store.phonebank.services.PhoneBookingQueryService;
import com.store.phonebank.services.PhoneBookingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/phone-booking/")
@Api(value = "Phone Booking", tags = {"Phone Booking"})
public class PhoneBookingController {

    private final PhoneBookingService phoneBookingService;
    private final PhoneBookingQueryService phoneBookingQueryService;

    public PhoneBookingController(PhoneBookingService phoneBookingService, PhoneBookingQueryService phoneBookingQueryService) {
        this.phoneBookingService = phoneBookingService;
        this.phoneBookingQueryService = phoneBookingQueryService;
    }

    @PostMapping("/book")
    @ApiOperation(value = "Book a phone")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully booked the phone"),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public Mono<ResponseEntity<PhoneBookingResponseDto>> bookPhone(@RequestParam String brandName, @RequestParam String modelCode, @RequestParam String userName) {
        PhoneBookingRequestDto phoneBookingRequestDto = PhoneBookingRequestDto.builder()
                .brandName(brandName)
                .modelCode(modelCode)
                .userName(userName)
                .build();

        return phoneBookingService.bookPhone(phoneBookingRequestDto)
                .map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                .onErrorResume(e -> Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR)));
    }

    @PostMapping("/return")
    @ApiOperation(value = "Return a booked phone")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully returned a booked phone"),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public Mono<ResponseEntity<PhoneReturnResponseDto>> returnPhone(@RequestParam String bookingId) {
        return phoneBookingService.returnBookedPhone(bookingId)
                .map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                .onErrorResume(e -> Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR)));
    }

    @GetMapping("/check-availability")
    @ApiOperation(value = "check availability for the phone")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "get availability of a phone by brand-name and model-code"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public Mono<ResponseEntity<PhoneAvailabilityResponseDto>> checkAvailability(@RequestParam String brandName, @RequestParam String modelCode) {
        return phoneBookingQueryService.checkPhoneAvailability(brandName, modelCode)
                .map(response -> new ResponseEntity<>(response, HttpStatus.OK))
                .onErrorResume(e -> Mono.just(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR)));
    }


}