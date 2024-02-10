package com.store.phonebank.controller;

import com.store.phonebank.entity.DeviceInfoEntity;
import com.store.phonebank.repository.DeviceInfoRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/device-info")
@Api(value = "Device Info Query", tags = {"Device Info-Query"})
public class DeviceInfoQueryController {

    private final DeviceInfoRepository deviceInfoRepository;

    public DeviceInfoQueryController(DeviceInfoRepository deviceInfoRepository) {
        this.deviceInfoRepository = deviceInfoRepository;
    }

    @GetMapping("/one")
    @ApiOperation(value = "get information a device by its id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "get complete information of a device by id"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public Mono<DeviceInfoEntity> getDeviceInfo(UUID deviceId) {
        return this.deviceInfoRepository.findById(deviceId);
    }

    @GetMapping("/all")
    @ApiOperation(value = "get information of all devices")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "get complete information of all devices"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public Flux<DeviceInfoEntity> getAllDevices() {
        return this.deviceInfoRepository.findAll();
    }
}
