package com.store.phonebank.services.query;

import com.store.phonebank.entity.DeviceInfoEntity;
import reactor.core.publisher.Mono;

public interface IDeviceInfoService {
    public Mono<DeviceInfoEntity> getDeviceInfo(String brandName, String modelCode);

    public Mono<DeviceInfoEntity> updateDeviceInfo(DeviceInfoEntity deviceInfoEntity);
}
