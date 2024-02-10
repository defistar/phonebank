package com.store.phonebank.repository;

import com.store.phonebank.entity.PhoneBookingEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface PhoneBookingRepository extends ReactiveCrudRepository<PhoneBookingEntity, UUID> {

    Mono<PhoneBookingEntity> findTopByPhoneEntityIdAndIsReturnedOrderByBookingTimeDesc(UUID phoneEntityId, boolean isReturned);

    Mono<PhoneBookingEntity> findTopByPhoneEntityIdAndIsReturnedFalseOrderByBookingTimeDesc(UUID phoneEntityId);

    Mono<PhoneBookingEntity> findTopByPhoneEntityIdAndIsReturnedTrueOrderByBookingTimeDesc(UUID phoneEntityId);

    default Mono<PhoneBookingEntity> findLastBookedOrReturnedPhoneByEntityId(UUID phoneEntityId) {
        return findTopByPhoneEntityIdAndIsReturnedFalseOrderByBookingTimeDesc(phoneEntityId)
                .switchIfEmpty(findTopByPhoneEntityIdAndIsReturnedTrueOrderByBookingTimeDesc(phoneEntityId));
    }
}