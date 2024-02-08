package com.store.phonebank.dto;

import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class PhoneBookingRequestDto {
    @NotNull(message = "Brand name is required")
    private String brandName;

    @NotNull(message = "Model code is required")
    private String modelCode;

    @NotNull(message = "User name is required")
    private String userName;
}