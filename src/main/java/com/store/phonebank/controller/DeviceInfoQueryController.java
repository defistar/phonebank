package com.store.phonebank.controller;

import com.store.phonebank.entity.DeviceInfoEntity;
import com.store.phonebank.repository.DeviceInfoRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/device-info")
@Tag(name = "Device Info Query", description = "Device Info-Query")
public class DeviceInfoQueryController {

    private final DeviceInfoRepository deviceInfoRepository;

    public DeviceInfoQueryController(DeviceInfoRepository deviceInfoRepository) {
        this.deviceInfoRepository = deviceInfoRepository;
    }

    @GetMapping("/one")
    @Operation(summary = "get information a device by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "get complete information of a device by id"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<DeviceInfoEntity> getDeviceInfo(UUID deviceId) {
        return this.deviceInfoRepository.findById(deviceId);
    }

    @GetMapping("/all")
    @Operation(summary = "get information of all devices")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "get complete information of all devices"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Flux<DeviceInfoEntity> getAllDevices() {
        return this.deviceInfoRepository.findAll();
    }
}
