package com.store.phonebank.services.booking;

import com.store.phonebank.dto.PhoneReturnResponseDto;
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

@Service
public class PhoneReturnService implements IPhoneReturnService {
    private static final Logger logger = LoggerFactory.getLogger(PhoneReturnService.class);

    private final PhoneRepository phoneRepository;
    private final PhoneBookingRepository phoneBookingRepository;

    private final TransactionalOperator transactionalOperator;

    public PhoneReturnService(PhoneRepository phoneRepository, PhoneBookingRepository phoneBookingRepository, TransactionalOperator transactionalOperator) {
        this.phoneRepository = phoneRepository;
        this.phoneBookingRepository = phoneBookingRepository;
        this.transactionalOperator = transactionalOperator;
    }

    public Mono<ResponseEntity<PhoneReturnResponseDto>> returnBookedPhone(UUID bookingId) {
        logger.info("Returning phone with booking id: " + bookingId);
        return this.phoneBookingRepository.findById(bookingId)
                .switchIfEmpty(handleNonExistentBooking(bookingId))
                .filter(phoneBookingEntity -> !phoneBookingEntity.isReturned())
                .switchIfEmpty(handleAlreadyReturnedBooking(bookingId))
                .flatMap(this::processPhoneReturn)
                .map(ResponseEntity::ok)
                .onErrorResume(DataAccessException.class, ex -> handleDataAccessException(ex, bookingId))
                .onErrorResume(RuntimeException.class, ex -> handleRuntimeException(ex, bookingId))
                .as(transactionalOperator::transactional); // apply transactional operator;
    }

    private Mono<PhoneBookingEntity> handleNonExistentBooking(UUID bookingId) {
        return Mono.error(new RuntimeException("Phone booking with id " + bookingId + " does not exist"));
    }

    private Mono<PhoneBookingEntity> handleAlreadyReturnedBooking(UUID bookingId) {
        return Mono.error(new RuntimeException("Phone booking with id " + bookingId + " has already been returned"));
    }

    private Mono<PhoneReturnResponseDto> processPhoneReturn(PhoneBookingEntity phoneBookingEntity) {
        phoneBookingEntity.setReturned(true);
        phoneBookingEntity.setUpdatedAt(LocalDateTime.now());
        return this.phoneBookingRepository.save(phoneBookingEntity)
                .flatMap(updatedPhoneBooking -> this.phoneRepository.findById(updatedPhoneBooking.getPhoneEntityId())
                        .flatMap(phone -> {
                            phone.setAvailableCount(phone.getAvailableCount() + 1);
                            return this.phoneRepository.save(phone)
                                    .map(updatedPhone -> this.toReturnResponseDto(updatedPhone, updatedPhoneBooking));
                        })
                );
    }

    private Mono<ResponseEntity<PhoneReturnResponseDto>> handleDataAccessException(DataAccessException ex, UUID bookingId) {
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new PhoneReturnResponseDto(bookingId, "Failed", ex.getMessage())));
    }

    private Mono<ResponseEntity<PhoneReturnResponseDto>> handleRuntimeException(RuntimeException ex, UUID bookingId) {
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new PhoneReturnResponseDto(bookingId, "Failed", ex.getMessage())));
    }

    private PhoneReturnResponseDto toReturnResponseDto(PhoneEntity phoneEntity, PhoneBookingEntity phoneBooking) {
        PhoneReturnResponseDto responseDto = new PhoneReturnResponseDto();
        responseDto.setPhoneBookingId(phoneBooking.getId());
        responseDto.setPhoneEntityId(phoneEntity.getId());
        responseDto.setBrandName(phoneEntity.getBrandName());
        responseDto.setModelCode(phoneEntity.getModelCode());
        responseDto.setAvailability(phoneEntity.getAvailableCount() > 0 ? "Yes" : "No");
        responseDto.setLastBookedAt(phoneBooking.getBookingTime());
        responseDto.setLastBookedUser(phoneBooking.getUserName());
        responseDto.setReturned(phoneBooking.isReturned());
        return responseDto;
    }
}