package com.store.phonebank.services;

import com.store.phonebank.dto.PhoneDto;
import com.store.phonebank.entity.PhoneEntity;
import com.store.phonebank.repository.PhoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PhoneOnboardingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PhoneOnboardingService.class);

    private final PhoneRepository phoneRepository;

    public PhoneOnboardingService(PhoneRepository phoneRepository) {
        this.phoneRepository = phoneRepository;
    }

    public Mono<PhoneDto> savePhone(PhoneDto phoneDto) {
        PhoneEntity phoneEntity = toEntity(phoneDto);

        return this.phoneRepository.findByBrandNameAndModelCode(phoneDto.getBrandName(), phoneDto.getModelCode())
                .flatMap(existingPhone -> {
                    phoneEntity.setId(existingPhone.getId()); // Ensure the ID is set for existing entities
                    return this.phoneRepository.save(phoneEntity);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    phoneEntity.setId(UUID.randomUUID().toString()); // Set ID to a new UUID for new entities
                    if (phoneEntity.getCreatedAt() == null) {
                        phoneEntity.setCreatedAt(LocalDateTime.now());
                    }
                    return this.phoneRepository.insert(phoneEntity);
                }))
                .map(this::toDto);
    }

    private PhoneEntity toEntity(PhoneDto phoneDto) {
        PhoneEntity phoneEntity = new PhoneEntity();
        phoneEntity.setId(phoneDto.getId());
        phoneEntity.setBrandName(phoneDto.getBrandName());
        phoneEntity.setModelName(phoneDto.getModelName());
        phoneEntity.setModelCode(phoneDto.getModelCode());
        phoneEntity.setPhoneCount(phoneDto.getPhoneCount());
        phoneEntity.setAvailableCount(phoneDto.getPhoneCount());
        return phoneEntity;
    }

    private PhoneDto toDto(PhoneEntity phoneEntity) {
        PhoneDto phoneDto = new PhoneDto();
        phoneDto.setId(phoneEntity.getId());
        phoneDto.setBrandName(phoneEntity.getBrandName());
        phoneDto.setModelName(phoneEntity.getModelName());
        phoneDto.setModelCode(phoneEntity.getModelCode());
        phoneDto.setPhoneCount(phoneEntity.getPhoneCount());
        phoneDto.setAvailableCount(phoneEntity.getAvailableCount());
        return phoneDto;
    }
}