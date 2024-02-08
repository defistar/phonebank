package com.store.phonebank.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PhoneReturnResponseDto {
    private String phoneBookingId;
    private String phoneEntityId;
    private String brandName;
    private String modelCode;
    private String availability;
    private LocalDateTime whenBooked;
    private String whoBooked;
    private boolean returned;
    private String responseCode;
    private String errorMessage;

    public PhoneReturnResponseDto(String phoneBookingId, String failed, String message) {
        this.phoneBookingId = phoneBookingId;
        this.returned = false;
        this.responseCode = failed;
        this.errorMessage = message;
    }
}