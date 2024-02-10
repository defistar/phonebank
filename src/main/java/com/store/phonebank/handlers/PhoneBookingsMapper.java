package com.store.phonebank.handlers;

import com.store.phonebank.dto.DeviceInfoDto;
import com.store.phonebank.dto.PhoneBookingDto;
import com.store.phonebank.entity.DeviceInfoEntity;
import com.store.phonebank.entity.PhoneBookingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PhoneBookingsMapper {
    PhoneBookingsMapper INSTANCE = Mappers.getMapper(PhoneBookingsMapper.class);

    PhoneBookingDto toPhoneBookingDto(PhoneBookingEntity phoneBookingEntity);

    DeviceInfoDto toDeviceInfoDto(DeviceInfoEntity deviceInfoEntity);
}