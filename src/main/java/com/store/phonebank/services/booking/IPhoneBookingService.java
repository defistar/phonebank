package com.store.phonebank.services.booking;

import com.store.phonebank.dto.PhoneBookingRequestDto;
import com.store.phonebank.dto.PhoneBookingResponseDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface IPhoneBookingService {
    Mono<ResponseEntity<PhoneBookingResponseDto>> bookPhone(PhoneBookingRequestDto phoneBookingRequestDto);
}