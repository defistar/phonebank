package com.store.phonebank.services.booking;

import com.store.phonebank.dto.PhoneAvailabilityResponseDto;
import com.store.phonebank.dto.PhoneBookingDto;
import com.store.phonebank.handlers.PhoneBookingsMapper;
import com.store.phonebank.repository.PhoneBookingRepository;
import com.store.phonebank.repository.PhoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class PhoneBookingQueryService implements IPhoneBookingQueryService {

    private static final Logger logger = LoggerFactory.getLogger(PhoneBookingQueryService.class);
    private final PhoneRepository phoneRepository;
    private final PhoneBookingRepository phoneBookingRepository;

    public PhoneBookingQueryService(PhoneRepository phoneRepository, PhoneBookingRepository phoneBookingRepository) {
        this.phoneRepository = phoneRepository;
        this.phoneBookingRepository = phoneBookingRepository;
    }

    public Mono<PhoneAvailabilityResponseDto> checkPhoneAvailability(String brandName, String modelCode) {
//        return this.phoneRepository.findByBrandNameAndModelCode(brandName, modelCode)
//                .zipWhen(phoneEntity->{
//                    System.out.printf("PhoneEntity: %s", phoneEntity);
//                    return this.phoneBookingRepository
//                            .findTopByPhoneEntityIdAndIsReturnedOrderByBookingTimeDesc(phoneEntity.getId(), false);
//
//                })
//                .flatMap(tuple -> {
//                    System.out.printf("PhoneEntity: %s, PhoneBooking: %s", tuple.getT1(), tuple.getT2());
//                    return  Mono.just(new PhoneAvailabilityResponseDto());
//                    }).onErrorResume(e -> {
//                    logger.error("Error occurred while checking phone availability", e);
//                    return Mono.just(new PhoneAvailabilityResponseDto());
//              });


        return
                this.phoneBookingRepository  .findTopByPhoneEntityIdAndIsReturnedOrderByBookingTimeDesc("66188f5a-67d2-4acb-8f83-8137f05b0cd7::uuid", false)
                .flatMap(tuple -> {
                    System.out.printf("PhoneEntity: %s", tuple);
                    return  Mono.just(new PhoneAvailabilityResponseDto());
                }).onErrorResume(e -> {
                    logger.error("Error occurred while checking phone availability", e);
                    return Mono.just(new PhoneAvailabilityResponseDto());
                });
    }
//    }
//    public Mono<PhoneAvailabilityResponseDto> checkPhoneAvailability(String brandName, String modelCode) {
//        return this.phoneRepository.findByBrandNameAndModelCode(brandName, modelCode)
//                .zipWhen(phoneEntity->{
//                    phoneEntity.getId()
//                })
//                .flatMap(phoneEntity -> {
//                    PhoneAvailabilityResponseDto responseDto = new PhoneAvailabilityResponseDto();
//                    responseDto.setPhoneEntityId(phoneEntity.getId());
//                    responseDto.setBrandName(phoneEntity.getBrandName());
//                    responseDto.setModelCode(phoneEntity.getModelCode());
//                    responseDto.setAvailability(phoneEntity.getAvailableCount() > 0 ? "Yes" : "No");
//                    return this.phoneBookingRepository
//                            .findTopByPhoneEntityIdAndIsReturnedOrderByBookingTimeDesc(""+phoneEntity.getId(), false)
//                            .doOnNext(phoneBooking -> {
//                                if (phoneBooking != null) {
//                                    responseDto.setWhenLastBooked(phoneBooking.getBookingTime());
//                                    responseDto.setLastBookedUser(phoneBooking.getUserName());
//                                }
//                            })
//                            .thenReturn(responseDto);
//                })
//                .onErrorResume(e -> {
//                    logger.error("Error occurred while checking phone availability", e);
//                    return Mono.just(new PhoneAvailabilityResponseDto());
//                })
//                .switchIfEmpty(Mono.just(new PhoneAvailabilityResponseDto()));
//    }

    public Mono<PhoneBookingDto> findCurrentActiveBookingDetails(UUID phoneEntityId) {
        return phoneBookingRepository.findTopByPhoneEntityIdAndIsReturnedFalseOrderByBookingTimeDesc(phoneEntityId)
                .flatMap(phoneBookingEntity -> {
                    if (phoneBookingEntity != null) {
                        return Mono.just(PhoneBookingsMapper.INSTANCE.toPhoneBookingDto(phoneBookingEntity));
                    } else {
                        return Mono.just(new PhoneBookingDto());
                    }
                });
    }

    public Mono<PhoneBookingDto> findLastBookingDetails(UUID phoneEntityId) {
        return phoneBookingRepository.findTopByPhoneEntityIdAndIsReturnedTrueOrderByBookingTimeDesc(phoneEntityId)
                .flatMap(phoneBookingEntity -> {
                    if (phoneBookingEntity != null) {
                        return Mono.just(PhoneBookingsMapper.INSTANCE.toPhoneBookingDto(phoneBookingEntity));
                    } else {
                        return Mono.just(new PhoneBookingDto());
                    }
                });
    }

    public Mono<PhoneBookingDto> findCurrentActiveOrLastBookingDetails(UUID phoneEntityId) {
        return phoneBookingRepository.findLastBookedOrReturnedPhoneByEntityId(phoneEntityId)
                .flatMap(phoneBookingEntity -> {
                    if (phoneBookingEntity != null) {
                        return Mono.just(PhoneBookingsMapper.INSTANCE.toPhoneBookingDto(phoneBookingEntity));
                    } else {
                        return Mono.just(new PhoneBookingDto());
                    }
                });
    }
}