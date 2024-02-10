package com.store.phonebank.controller;

import com.store.phonebank.dto.PhoneDto;
import com.store.phonebank.services.query.PhoneInfoQueryService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/phone-info")
@Tag(name = "Phone Info Query", description = "Phone Info-Query API")
public class PhoneInfoQueryController {

    private final PhoneInfoQueryService phoneInfoQueryService;

    public PhoneInfoQueryController(PhoneInfoQueryService phoneInfoQueryService) {
        this.phoneInfoQueryService = phoneInfoQueryService;
    }

    @GetMapping("/one")
    @Operation(summary = "get information a phone by its brand-name and model-code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "get complete information of a phone by brand-name and model-code"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<PhoneDto> getPhoneInfo(String brandName, String modelCode) {
        return this.phoneInfoQueryService.getPhoneInfo(brandName, modelCode);
    }

    @GetMapping("/all")
    @Operation(summary = "get information of all phones")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "get complete information of all phones"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Flux<PhoneDto> getAllPhones() {
        return this.phoneInfoQueryService.getAllPhones();
    }
}
