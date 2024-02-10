package com.store.phonebank.services.booking;

import com.store.phonebank.config.BookingStatus;
import com.store.phonebank.dto.PhoneBookingRequestDto;
import com.store.phonebank.dto.PhoneBookingResponseDto;
import com.store.phonebank.entity.PhoneBookingEntity;
import com.store.phonebank.entity.PhoneEntity;
import com.store.phonebank.repository.PhoneBookingRepository;
import com.store.phonebank.repository.PhoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.UUID;
@Service
public class PhoneBookingService implements IPhoneBookingService {
    private static final Logger logger = LoggerFactory.getLogger(PhoneBookingService.class);

    private final PhoneRepository phoneRepository;
    private final PhoneBookingRepository phoneBookingRepository;

    private final TransactionalOperator transactionalOperator;

    public PhoneBookingService(PhoneRepository phoneRepository, PhoneBookingRepository phoneBookingRepository, TransactionalOperator transactionalOperator) {
        this.phoneRepository = phoneRepository;
        this.phoneBookingRepository = phoneBookingRepository;
        this.transactionalOperator = transactionalOperator;
    }

    public Mono<ResponseEntity<PhoneBookingResponseDto>> bookPhone(PhoneBookingRequestDto phoneBookingRequestDto) {
        return this.phoneRepository.findByBrandNameAndModelCode(phoneBookingRequestDto.getBrandName(), phoneBookingRequestDto.getModelCode())
                .flatMap(phone -> processPhoneBooking(phone, phoneBookingRequestDto))
                .switchIfEmpty(handleEmptyPhone(phoneBookingRequestDto))
                .onErrorResume(DataAccessException.class, ex -> handleDataAccessException(ex, phoneBookingRequestDto))
                .onErrorResume(RuntimeException.class, ex -> handleRuntimeException(ex, phoneBookingRequestDto))
                .as(transactionalOperator::transactional)
                .map(this::createResponseEntity);
    }

    private Mono<PhoneBookingResponseDto> processPhoneBooking(PhoneEntity phone, PhoneBookingRequestDto phoneBookingRequestDto) {
        logger.info("Processing phone booking for phone: " + phone);
        if (phone.getAvailableCount() > 0) {
            logger.info("Phone available for booking: " + phone);
            phone.setAvailableCount(phone.getAvailableCount() - 1);
            return this.phoneRepository.save(phone)
                    .flatMap(updatedPhone -> {
                        logger.info("Phone updated: " + updatedPhone);
                        PhoneBookingEntity phoneBooking = new PhoneBookingEntity();
                        phoneBooking.setCreatedAt(LocalDateTime.now());
                        phoneBooking.setPhoneEntityId(updatedPhone.getId());
                        phoneBooking.setUserName(phoneBookingRequestDto.getUserName());
                        phoneBooking.setBookingTime(LocalDateTime.now());
                        return this.phoneBookingRepository.save(phoneBooking)
                                .flatMap(phoneBookingSaved -> {
                                    PhoneBookingResponseDto responseDto = this.toBookingResponseDto(phoneBookingSaved, updatedPhone);
                                    responseDto.setBookingStatus(BookingStatus.SUCCESSFUL);
                                    return Mono.just(responseDto);
                                });
                    });
        } else {
            logger.info("about to query phonebooking for phone with id: " + phone.getId());
            return this.phoneBookingRepository.findTopByPhoneEntityIdAndIsReturnedOrderByBookingTimeDesc(phone.getId(), false)
                    .map(lastBooking -> {
                        PhoneBookingResponseDto responseDto = new PhoneBookingResponseDto();
                        responseDto.setPhoneEntityId(phone.getId());
                        responseDto.setBrandName(phone.getBrandName());
                        responseDto.setModelCode(phone.getModelCode());
                        responseDto.setAvailableCount(phone.getAvailableCount());
                        responseDto.setPhoneCount(phone.getPhoneCount());
                        responseDto.setLastBookedAt(lastBooking.getBookingTime());
                        responseDto.setLastBookedUser(lastBooking.getUserName());
                        responseDto.setBookingStatus(BookingStatus.FAILED_PHONE_NOT_AVAILABLE);
                        return responseDto;
                    });
        }

    }

    private Mono<PhoneBookingResponseDto> handleEmptyPhone(PhoneBookingRequestDto phoneBookingRequestDto) {
        PhoneBookingResponseDto responseDto = new PhoneBookingResponseDto();
        responseDto.setBrandName(phoneBookingRequestDto.getBrandName());
        responseDto.setModelCode(phoneBookingRequestDto.getModelCode());
        responseDto.setBookingStatus(BookingStatus.INVALID_PHONE);
        return Mono.just(responseDto);
    }

    private Mono<PhoneBookingResponseDto> handleDataAccessException(DataAccessException ex, PhoneBookingRequestDto phoneBookingRequestDto) {
        logger.error("Error occurred while booking phone", ex);
        PhoneBookingResponseDto responseDto = new PhoneBookingResponseDto(phoneBookingRequestDto.getBrandName(), phoneBookingRequestDto.getModelCode(), "Failed", ex.getMessage());
        return Mono.just(responseDto);
    }

    private Mono<PhoneBookingResponseDto> handleRuntimeException(RuntimeException ex, PhoneBookingRequestDto phoneBookingRequestDto) {
        logger.error("Error occurred while booking phone", ex);
        PhoneBookingResponseDto responseDto = new PhoneBookingResponseDto(phoneBookingRequestDto.getBrandName(), phoneBookingRequestDto.getModelCode(), "Failed", ex.getMessage());
        return Mono.just(responseDto);
    }

    private ResponseEntity<PhoneBookingResponseDto> createResponseEntity(PhoneBookingResponseDto responseDto) {
        if (responseDto.getBookingStatus() == BookingStatus.SUCCESSFUL) {
            return ResponseEntity.ok(responseDto);
        } else if (responseDto.getBookingStatus() == BookingStatus.INVALID_PHONE) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
        }
    }

    private PhoneBookingResponseDto toBookingResponseDto(PhoneBookingEntity phoneBooking, PhoneEntity phoneEntity) {
        PhoneBookingResponseDto responseDto = new PhoneBookingResponseDto();
        responseDto.setPhoneBookingId(phoneBooking.getId());
        responseDto.setPhoneEntityId(phoneBooking.getPhoneEntityId());
        responseDto.setBrandName(phoneEntity.getBrandName());
        responseDto.setModelCode(phoneEntity.getModelCode());
        responseDto.setAvailableCount(phoneEntity.getAvailableCount());
        responseDto.setPhoneCount(phoneEntity.getPhoneCount());
        responseDto.setLastBookedAt(phoneBooking.getBookingTime());
        responseDto.setLastBookedUser(phoneBooking.getUserName());
        return responseDto;
    }
}