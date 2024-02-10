package com.store.phonebank.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class PhoneDto {
    @NotNull
    private String id;

    @NotNull
    private String brandName;

    @NotNull
    private String modelName;

    @NotNull
    private String modelCode;

    @NotNull
    private int phoneCount;

    @NotNull
    private int availableCount;

    private DeviceInfoDto deviceInfo;

    private String lastBookedBy;

    private LocalDateTime lastBookedAt;
}