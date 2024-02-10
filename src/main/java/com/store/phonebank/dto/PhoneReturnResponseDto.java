package com.store.phonebank.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PhoneReturnResponseDto {
    private UUID phoneBookingId;
    private UUID phoneEntityId;
    private String brandName;
    private String modelCode;
    private String availability;
    private LocalDateTime lastBookedAt;
    private String lastBookedUser;
    private boolean returned;
    private String responseCode;
    private String errorMessage;

    public PhoneReturnResponseDto(UUID phoneBookingId, String failed, String message) {
        this.phoneBookingId = phoneBookingId;
        this.returned = false;
        this.responseCode = failed;
        this.errorMessage = message;
    }
}