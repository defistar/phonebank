package com.store.phonebank.services.query;

import com.store.phonebank.entity.DeviceInfoEntity;
import com.store.phonebank.repository.DeviceInfoRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class DeviceInfoService {
    private final DeviceInfoRepository deviceInfoRepository;

    public DeviceInfoService(DeviceInfoRepository deviceInfoRepository) {
        this.deviceInfoRepository = deviceInfoRepository;
    }

    @Cacheable("deviceInfo")
    public Mono<DeviceInfoEntity> getDeviceInfo(String brandName, String modelCode) {
        return deviceInfoRepository.findByBrandNameAndModelCode(brandName, modelCode);
    }

    public Mono<DeviceInfoEntity> updateDeviceInfo(DeviceInfoEntity deviceInfoEntity) {
        Mono<DeviceInfoEntity> updatedEntity = deviceInfoRepository.save(deviceInfoEntity);
        clearCache();
        return updatedEntity;
    }

    @CacheEvict(value = "deviceInfo", allEntries = true)
    public void clearCache() {
    }
}