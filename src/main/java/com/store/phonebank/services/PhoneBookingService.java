package com.store.phonebank.services;

import com.store.phonebank.dto.PhoneBookingRequestDto;
import com.store.phonebank.dto.PhoneBookingResponseDto;
import com.store.phonebank.dto.PhoneReturnResponseDto;
import com.store.phonebank.entity.PhoneBookingEntity;
import com.store.phonebank.entity.PhoneEntity;
import com.store.phonebank.repository.PhoneBookingRepository;
import com.store.phonebank.repository.PhoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PhoneBookingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PhoneBookingService.class);

    private final PhoneRepository phoneRepository;
    private final PhoneBookingRepository phoneBookingRepository;

    public PhoneBookingService(PhoneRepository phoneRepository, PhoneBookingRepository phoneBookingRepository) {
        this.phoneRepository = phoneRepository;
        this.phoneBookingRepository = phoneBookingRepository;
    }


    public Mono<PhoneBookingResponseDto> bookPhone(PhoneBookingRequestDto phoneBookingRequestDto) {
        return this.phoneRepository.findByBrandNameAndModelCode(phoneBookingRequestDto.getBrandName(), phoneBookingRequestDto.getModelCode())
                .filter(phone -> phone.getAvailableCount() > 0)
                .flatMap(phone -> {
                    phone.setAvailableCount(phone.getAvailableCount() - 1);
                    return this.phoneRepository.save(phone);
                })
                .flatMap(phone -> {
                    PhoneBookingEntity phoneBooking = new PhoneBookingEntity();
                    phoneBooking.setId(UUID.randomUUID().toString()); // Set ID to a new UUID for new entities
                    phoneBooking.setCreatedAt(LocalDateTime.now());
                    phoneBooking.setPhoneEntityId(phone.getId());
                    phoneBooking.setUserName(phoneBookingRequestDto.getUserName());
                    phoneBooking.setBookingTime(LocalDateTime.now());
                    return this.phoneBookingRepository.insert(phoneBooking)
                            .doOnSuccess(phoneBookingSaved -> {
                                if (phoneBookingSaved == null) {
                                    LOGGER.info("insert completed without emitting an item");
                                } else {
                                    LOGGER.info("insert completed and emitted an item");
                                }
                            })
                            .doOnError(e -> LOGGER.info("Error during insert: " + e.getMessage()))
                            .map(phoneBookingSaved -> this.toBookingResponseDto(phoneBookingSaved, phone));
                })
                .onErrorResume(DataAccessException.class, ex -> {
                    ex.printStackTrace();
                    return Mono.just(new PhoneBookingResponseDto());
                })
                .switchIfEmpty(Mono.just(new PhoneBookingResponseDto()));
    }

    private PhoneBookingResponseDto toBookingResponseDto(PhoneBookingEntity phoneBooking, PhoneEntity phoneEntity) {
        PhoneBookingResponseDto responseDto = new PhoneBookingResponseDto();
        responseDto.setPhoneBookingId(phoneBooking.getId());
        responseDto.setPhoneEntityId(phoneBooking.getPhoneEntityId());
        responseDto.setBrandName(phoneEntity.getBrandName());
        responseDto.setModelCode(phoneEntity.getModelCode());
        responseDto.setAvailability(phoneEntity.getAvailableCount() > 0 ? "Yes" : "No");
        responseDto.setWhenBooked(phoneBooking.getBookingTime());
        responseDto.setWhoBooked(phoneBooking.getUserName());
        return responseDto;
    }

    public Mono<PhoneReturnResponseDto> returnBookedPhone(String bookingId) {
        return this.phoneBookingRepository.findById(bookingId)
                .switchIfEmpty(Mono.error(new RuntimeException("Phone booking with id " + bookingId + " does not exist")))
                .filter(phoneBookingEntity -> !phoneBookingEntity.isReturned())
                .switchIfEmpty(Mono.error(new RuntimeException("Phone booking with id " + bookingId + " has already been returned")))
                .flatMap(phoneBookingEntity -> {
                    phoneBookingEntity.setReturned(true);
                    phoneBookingEntity.setUpdatedAt(LocalDateTime.now());
                    return this.phoneBookingRepository.save(phoneBookingEntity)
                            .flatMap(updatedPhoneBooking -> this.phoneRepository.findById(updatedPhoneBooking.getPhoneEntityId())
                                    .flatMap(phone -> {
                                        phone.setAvailableCount(phone.getAvailableCount() + 1);
                                        return this.phoneRepository.save(phone)
                                                .map(updatedPhone -> this.toReturnResponseDto(updatedPhone, updatedPhoneBooking));
                                    }));
                })
                .onErrorResume(DataAccessException.class, ex -> {
                    ex.printStackTrace();
                    return Mono.just(new PhoneReturnResponseDto(bookingId, "Failed", ex.getMessage()));
                })
                .onErrorResume(RuntimeException.class, ex -> {
                    ex.printStackTrace();
                    return Mono.just(new PhoneReturnResponseDto(bookingId, "Failed", ex.getMessage()));
                });
    }

    private PhoneReturnResponseDto toReturnResponseDto(PhoneEntity phoneEntity, PhoneBookingEntity phoneBooking) {
        PhoneReturnResponseDto responseDto = new PhoneReturnResponseDto();
        responseDto.setPhoneBookingId(phoneBooking.getId());
        responseDto.setPhoneEntityId(phoneEntity.getId());
        responseDto.setBrandName(phoneEntity.getBrandName());
        responseDto.setModelCode(phoneEntity.getModelCode());
        responseDto.setAvailability(phoneEntity.getAvailableCount() > 0 ? "Yes" : "No");
        responseDto.setWhenBooked(phoneBooking.getBookingTime());
        responseDto.setWhoBooked(phoneBooking.getUserName());
        responseDto.setReturned(phoneBooking.isReturned());
        return responseDto;
    }
}