package com.store.phonebank.services.booking;

import com.store.phonebank.dto.PhoneReturnResponseDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface IPhoneReturnService {
    Mono<ResponseEntity<PhoneReturnResponseDto>> returnBookedPhone(String bookingId);
}