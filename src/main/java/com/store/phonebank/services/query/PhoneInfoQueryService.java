package com.store.phonebank.services.query;

import com.store.phonebank.dto.PhoneDto;
import com.store.phonebank.handlers.PhoneMapper;
import com.store.phonebank.repository.PhoneRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PhoneInfoQueryService {

    private final DeviceInfoService deviceInfoService;
    private final PhoneRepository phoneRepository;

    public PhoneInfoQueryService(DeviceInfoService deviceInfoService, PhoneRepository phoneRepository) {
        this.deviceInfoService = deviceInfoService;
        this.phoneRepository = phoneRepository;
    }

    public Mono<PhoneDto> getPhoneInfo(String brandName, String modelCode) {
        return deviceInfoService.getDeviceInfo(brandName, modelCode)
                .flatMap(deviceInfo -> phoneRepository.findByBrandNameAndModelCode(brandName, modelCode)
                        .map(phoneEntity -> {
                            PhoneDto phoneDto = PhoneMapper.INSTANCE.toPhoneDto(phoneEntity);
                            phoneDto.setDeviceInfo(PhoneMapper.INSTANCE.toDeviceInfoDto(deviceInfo));
                            return phoneDto;
                        }))
                .switchIfEmpty(Mono.error(new RuntimeException("Phone not found")));
    }

    public Flux<PhoneDto> getAllPhones() {
        return phoneRepository.findAll()
                .flatMap(phoneEntity -> deviceInfoService.getDeviceInfo(phoneEntity.getBrandName(), phoneEntity.getModelCode())
                        .map(deviceInfo -> {
                            PhoneDto phoneDto = PhoneMapper.INSTANCE.toPhoneDto(phoneEntity);
                            phoneDto.setDeviceInfo(PhoneMapper.INSTANCE.toDeviceInfoDto(deviceInfo));
                            return phoneDto;
                        }));
    }
}
