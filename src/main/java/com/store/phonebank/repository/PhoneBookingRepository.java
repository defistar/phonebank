package com.store.phonebank.repository;

import com.store.phonebank.entity.PhoneBookingEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface PhoneBookingRepository extends ReactiveCrudRepository<PhoneBookingEntity, UUID> {

    Mono<PhoneBookingEntity> findTopByPhoneEntityIdAndIsReturnedOrderByBookingTimeDesc(String phoneEntityId, boolean isReturned);

    Mono<PhoneBookingEntity> findTopByPhoneEntityIdAndIsReturnedOrderByBookingTimeDesc(UUID phoneEntityId, boolean isReturned);

    Mono<PhoneBookingEntity> findTopByPhoneEntityIdAndIsReturnedFalseOrderByBookingTimeDesc(UUID phoneEntityId);

    Mono<PhoneBookingEntity> findTopByPhoneEntityIdAndIsReturnedTrueOrderByBookingTimeDesc(UUID phoneEntityId);

    default Mono<PhoneBookingEntity> findLastBookedOrReturnedPhoneByEntityId(UUID phoneEntityId) {
        return findTopByPhoneEntityIdAndIsReturnedFalseOrderByBookingTimeDesc(phoneEntityId)
                .switchIfEmpty(findTopByPhoneEntityIdAndIsReturnedTrueOrderByBookingTimeDesc(phoneEntityId));
    }

    @Query("INSERT INTO phone_booking (id, phone_entity_id, user_name, is_returned, booking_time, created_at, updated_at) VALUES (:#{#phoneBookingEntity.id}, :#{#phoneBookingEntity.phoneEntityId}, :#{#phoneBookingEntity.userName}, :#{#phoneBookingEntity.isReturned}, :#{#phoneBookingEntity.bookingTime}, :#{#phoneBookingEntity.createdAt}, :#{#phoneBookingEntity.updatedAt}) RETURNING *")
    Mono<PhoneBookingEntity> insert(PhoneBookingEntity phoneBookingEntity);
}