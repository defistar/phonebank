package com.store.phonebank.services;

import org.springframework.dao.DataAccessException;
import com.store.phonebank.dto.PhoneBookingRequestDto;
import com.store.phonebank.dto.PhoneBookingResponseDto;
import com.store.phonebank.entity.PhoneEntity;
import com.store.phonebank.repository.PhoneRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class PhoneBookingService {
    private final PhoneRepository phoneRepository;

    public PhoneBookingService(PhoneRepository phoneRepository) {
        this.phoneRepository = phoneRepository;
    }

    public Mono<PhoneBookingResponseDto> bookPhone(PhoneBookingRequestDto phoneBookingRequestDto) {
        return this.phoneRepository.findByBrandNameAndModelCode(phoneBookingRequestDto.getBrandName(), phoneBookingRequestDto.getModelCode())
                .filter(phone -> phone.getAvailableCount() > 0)
                .flatMap(phone -> {
                    phone.setAvailableCount(phone.getAvailableCount() - 1);
                    phone.setBookedBy(phoneBookingRequestDto.getUserName());
                    phone.setBookingTime(LocalDateTime.now());
                    return this.phoneRepository.save(phone);
                })
                .map(this::toDto)
                //If an error occurs, return an empty PhoneBookingResponseDto
                // DataException is a generic exception thrown by Spring Data when a database error occurs
                .onErrorResume(DataAccessException.class, ex -> {
                    return Mono.just(new PhoneBookingResponseDto());
                })
                .switchIfEmpty(Mono.just(new PhoneBookingResponseDto())); //If no phone is found, return an empty PhoneBookingResponseDto
    }

    public Mono<PhoneBookingResponseDto> returnBookedPhone(PhoneBookingRequestDto phoneBookingRequestDto) {
        return this.phoneRepository.findByBrandNameAndModelCode(phoneBookingRequestDto.getBrandName(), phoneBookingRequestDto.getModelCode())
                .filter(phone -> phone.getBookedBy().equals(phoneBookingRequestDto.getUserName()))
                .flatMap(phone -> {
                    phone.setAvailableCount(phone.getAvailableCount() + 1);
                    return this.phoneRepository.save(phone);
                })
                .map(this::toDto)
                .switchIfEmpty(Mono.just(new PhoneBookingResponseDto())); // return a default PhoneBookingResponseDto when no phone is found or the phone is not booked by the user
    }

    public Mono<PhoneBookingResponseDto> checkPhoneAvailability(PhoneBookingRequestDto phoneBookingRequestDto) {
        return this.phoneRepository.findByBrandNameAndModelCode(phoneBookingRequestDto.getBrandName(), phoneBookingRequestDto.getModelCode())
                .map(this::toAvailabilityDto)
                .switchIfEmpty(Mono.just(new PhoneBookingResponseDto())); // return a default PhoneBookingResponseDto when no phone is available
    }

    private PhoneBookingResponseDto toAvailabilityDto(PhoneEntity phoneEntity) {
        PhoneBookingResponseDto responseDto = new PhoneBookingResponseDto();
        responseDto.setPhoneEntityId(phoneEntity.getId());
        responseDto.setBrandName(phoneEntity.getBrandName());
        responseDto.setModelCode(phoneEntity.getModelCode());
        responseDto.setAvailability(phoneEntity.getAvailableCount() > 0 ? "Yes" : "No");
        responseDto.setWhenBooked(phoneEntity.getBookingTime());
        return responseDto;
    }

    private PhoneBookingResponseDto toDto(PhoneEntity phoneEntity) {
        PhoneBookingResponseDto responseDto = new PhoneBookingResponseDto();
        responseDto.setPhoneEntityId(phoneEntity.getId());
        responseDto.setBrandName(phoneEntity.getBrandName());
        responseDto.setModelCode(phoneEntity.getModelCode());
        responseDto.setAvailability(phoneEntity.getAvailableCount() > 0 ? "Yes" : "No");
        responseDto.setWhenBooked(phoneEntity.getBookingTime());
        responseDto.setWhoBooked(phoneEntity.getBookedBy());
        return responseDto;
    }
}