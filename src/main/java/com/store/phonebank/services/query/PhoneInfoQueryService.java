package com.store.phonebank.services.query;

import com.store.phonebank.dto.PhoneDto;
import com.store.phonebank.entity.DeviceInfoEntity;
import com.store.phonebank.entity.PhoneEntity;
import com.store.phonebank.handlers.PhoneMapper;
import com.store.phonebank.repository.PhoneRepository;
import com.store.phonebank.services.booking.IPhoneBookingQueryService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PhoneInfoQueryService {

    private final IDeviceInfoService deviceInfoService;
    private final PhoneRepository phoneRepository;

    private final IPhoneBookingQueryService phoneBookingQueryService;

    public PhoneInfoQueryService(IDeviceInfoService deviceInfoService, PhoneRepository phoneRepository, IPhoneBookingQueryService phoneBookingQueryService) {
        this.deviceInfoService = deviceInfoService;
        this.phoneRepository = phoneRepository;
        this.phoneBookingQueryService = phoneBookingQueryService;
    }

    public Mono<PhoneDto> getPhoneInfo(String brandName, String modelCode) {
        return deviceInfoService.getDeviceInfo(brandName, modelCode)
                .flatMap(deviceInfo -> getPhoneByBrandAndModel(brandName, modelCode, deviceInfo))
                .flatMap(this::extractLatestBookingDetailsOfPhone)
                .switchIfEmpty(Mono.error(new RuntimeException("Phone not found")));
    }

    private Mono<PhoneDto> getPhoneByBrandAndModel(String brandName, String modelCode, DeviceInfoEntity deviceInfo) {
        return phoneRepository.findByBrandNameAndModelCode(brandName, modelCode)
                .map(phoneEntity -> {
                    PhoneDto phoneDto = PhoneMapper.INSTANCE.toPhoneDto(phoneEntity);
                    phoneDto.setDeviceInfo(PhoneMapper.INSTANCE.toDeviceInfoDto(deviceInfo));
                    return phoneDto;
                });
    }

    private Mono<PhoneDto> extractLatestBookingDetailsOfPhone(PhoneDto phoneDto) {
        return phoneBookingQueryService.findCurrentActiveOrLastBookingDetails(phoneDto.getId())
                .doOnNext(phoneBooking -> {
                    if (phoneBooking != null) {
                        phoneDto.setLastBookedAt(phoneBooking.getBookingTime());
                        phoneDto.setLastBookedBy(phoneBooking.getUserName());
                    }
                }).thenReturn(phoneDto);
    }

    public Flux<PhoneDto> getAllPhones() {
        return phoneRepository.findAll()
                .flatMap(this::getPhoneWithDeviceInfo)
                .flatMap(this::extractLatestBookingDetailsOfPhone);
    }

    private Mono<PhoneDto> getPhoneWithDeviceInfo(PhoneEntity phoneEntity) {
        return deviceInfoService.getDeviceInfo(phoneEntity.getBrandName(), phoneEntity.getModelCode())
                .map(deviceInfo -> {
                    PhoneDto phoneDto = PhoneMapper.INSTANCE.toPhoneDto(phoneEntity);
                    phoneDto.setDeviceInfo(PhoneMapper.INSTANCE.toDeviceInfoDto(deviceInfo));
                    return phoneDto;
                });
    }
}
