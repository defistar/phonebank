package com.store.phonebank.dto;

import lombok.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Data
public class DeviceInfoDto {
    @NotNull
    private String id;

    @NotNull
    private String brandName;

    @NotNull
    private String modelCode;

    private String technology;

    private String _2g_bands;

    private String _3g_bands;

    private String _4g_bands;
}