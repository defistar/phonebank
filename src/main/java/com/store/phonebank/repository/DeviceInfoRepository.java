package com.store.phonebank.repository;

import com.store.phonebank.entity.DeviceInfoEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface DeviceInfoRepository extends ReactiveCrudRepository<DeviceInfoEntity, String> {

    @Query("INSERT INTO device_info (id, brand_name, model_code, technology, _2g_bands, _3g_bands, _4g_bands, created_at, updated_at) VALUES (:#{#deviceInfoEntity.id}, :#{#deviceInfoEntity.brandName}, :#{#deviceInfoEntity.modelCode}, :#{#deviceInfoEntity.technology}, :#{#deviceInfoEntity._2g_bands}, :#{#deviceInfoEntity._3g_bands}, :#{#deviceInfoEntity._4g_bands}, :#{#deviceInfoEntity.createdAt}, :#{#deviceInfoEntity.updatedAt}) RETURNING *")
    Mono<DeviceInfoEntity> insert(DeviceInfoEntity deviceInfoEntity);

    Mono<DeviceInfoEntity> findByBrandNameAndModelCode(String brandName, String modelCode);
}
