package com.store.phonebank.services.booking;

import com.store.phonebank.dto.PhoneAvailabilityResponseDto;
import com.store.phonebank.dto.PhoneBookingDto;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IPhoneBookingQueryService {
    Mono<PhoneAvailabilityResponseDto> checkPhoneAvailability(String brandName, String modelCode);

    Mono<PhoneBookingDto> findCurrentActiveOrLastBookingDetails(UUID phoneEntityId);
}