package com.store.phonebank.repository;

import com.store.phonebank.entity.PhoneEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface PhoneRepository extends ReactiveCrudRepository<PhoneEntity, UUID> {

    Mono<PhoneEntity> findByBrandNameAndModelCode(String brandName, String modelCode);
}