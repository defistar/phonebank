package com.store.phonebank.services;

import com.store.phonebank.dto.PhoneAvailabilityResponseDto;
import com.store.phonebank.repository.PhoneBookingRepository;
import com.store.phonebank.repository.PhoneRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PhoneBookingQueryService {
    private final PhoneRepository phoneRepository;
    private final PhoneBookingRepository phoneBookingRepository;

    public PhoneBookingQueryService(PhoneRepository phoneRepository, PhoneBookingRepository phoneBookingRepository) {
        this.phoneRepository = phoneRepository;
        this.phoneBookingRepository = phoneBookingRepository;
    }

    public Mono<PhoneAvailabilityResponseDto> checkPhoneAvailability(String brandName, String modelCode) {
        return this.phoneRepository.findByBrandNameAndModelCode(brandName, modelCode)
                .flatMap(phoneEntity -> {
                    PhoneAvailabilityResponseDto responseDto = new PhoneAvailabilityResponseDto();
                    responseDto.setPhoneEntityId(phoneEntity.getId());
                    responseDto.setBrandName(phoneEntity.getBrandName());
                    responseDto.setModelCode(phoneEntity.getModelCode());
                    responseDto.setAvailability(phoneEntity.getAvailableCount() > 0 ? "Yes" : "No");
                    return this.phoneBookingRepository.findTopByPhoneEntityIdAndIsReturnedOrderByBookingTimeDesc(phoneEntity.getId(), false)
                            .doOnNext(phoneBooking -> {
                                if (phoneBooking != null) {
                                    responseDto.setWhenBooked(phoneBooking.getBookingTime());
                                    responseDto.setWhoBooked(phoneBooking.getUserName());
                                }
                            })
                            .thenReturn(responseDto);
                })
                .onErrorResume(e -> {
                    e.printStackTrace();
                    return Mono.just(new PhoneAvailabilityResponseDto());
                })
                .switchIfEmpty(Mono.just(new PhoneAvailabilityResponseDto()));
    }
}