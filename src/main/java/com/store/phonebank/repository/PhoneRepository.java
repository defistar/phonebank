package com.store.phonebank.repository;

import com.store.phonebank.entity.PhoneEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface PhoneRepository extends ReactiveCrudRepository<PhoneEntity, UUID> {
    @Query("INSERT INTO phone (id, brand_name, model_name, model_code, phone_count, available_count, created_at, updated_at) VALUES (:#{#phoneEntity.id}, :#{#phoneEntity.brandName}, :#{#phoneEntity.modelName}, :#{#phoneEntity.modelCode}, :#{#phoneEntity.phoneCount}, :#{#phoneEntity.availableCount}, :#{#phoneEntity.createdAt}, :#{#phoneEntity.updatedAt})")
    Mono<PhoneEntity> insert(PhoneEntity phoneEntity);

    Mono<PhoneEntity> findByBrandNameAndModelCode(String brandName, String modelCode);
}