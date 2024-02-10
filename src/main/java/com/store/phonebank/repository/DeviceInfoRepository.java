package com.store.phonebank.repository;

import com.store.phonebank.entity.DeviceInfoEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface DeviceInfoRepository extends ReactiveCrudRepository<DeviceInfoEntity, UUID> {

    Mono<DeviceInfoEntity> findByBrandNameAndModelCode(String brandName, String modelCode);
}
