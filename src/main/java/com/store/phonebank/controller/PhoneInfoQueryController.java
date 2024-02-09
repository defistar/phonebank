package com.store.phonebank.controller;

import com.store.phonebank.dto.PhoneDto;
import com.store.phonebank.services.query.PhoneInfoQueryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/phone-info")
@Api(value = "Phone Info Query", tags = {"Phone Info-Query"})
public class PhoneInfoQueryController {

    private final PhoneInfoQueryService phoneInfoQueryService;

    public PhoneInfoQueryController(PhoneInfoQueryService phoneInfoQueryService) {
        this.phoneInfoQueryService = phoneInfoQueryService;
    }

    @GetMapping("/one")
    @ApiOperation(value = "get information a phone by its brand-name and model-code")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "get complete information of a phone by brand-name and model-code"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public Mono<PhoneDto> getPhoneInfo(String brandName, String modelCode) {
        return this.phoneInfoQueryService.getPhoneInfo(brandName, modelCode);
    }

    @GetMapping("/all")
    @ApiOperation(value = "get information of all phones")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "get complete information of all phones"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public Flux<PhoneDto> getAllPhones() {
        return this.phoneInfoQueryService.getAllPhones();
    }
}
