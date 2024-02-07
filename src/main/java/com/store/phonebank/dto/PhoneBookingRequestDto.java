package com.store.phonebank.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PhoneBookingRequestDto {
    private String brandName;
    private String modelCode;
    private String userName;
}