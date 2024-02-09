package com.store.phonebank.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FonoDeviceInfoDto {

    @JsonProperty("DeviceName")
    private String deviceName;

    @JsonProperty("_2g_bands")
    private String twoGBands;

    @JsonProperty("_3g_bands")
    private String threeGBands;

    @JsonProperty("_4g_bands")
    private String fourGBands;
}

