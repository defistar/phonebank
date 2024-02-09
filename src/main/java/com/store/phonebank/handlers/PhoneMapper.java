package com.store.phonebank.handlers;

import com.store.phonebank.dto.DeviceInfoDto;
import com.store.phonebank.dto.PhoneDto;
import com.store.phonebank.entity.DeviceInfoEntity;
import com.store.phonebank.entity.PhoneEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PhoneMapper {
    PhoneMapper INSTANCE = Mappers.getMapper(PhoneMapper.class);

    PhoneDto toPhoneDto(PhoneEntity phoneEntity);

    DeviceInfoDto toDeviceInfoDto(DeviceInfoEntity deviceInfoEntity);
}