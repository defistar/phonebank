package com.store.phonebank.dto;

import com.store.phonebank.config.BookingStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PhoneBookingResponseDto {
    private String phoneBookingId;
    private String phoneEntityId;
    private String brandName;
    private String modelCode;
    private BookingStatus bookingStatus;
    private int availableCount;
    private int phoneCount;
    private LocalDateTime lastBookedAt;
    private String lastBookedUser;
    private String responseCode;
    private String errorMessage;

    public PhoneBookingResponseDto(String brandName, String modelCode, String failed, String message) {
        this.brandName = brandName;
        this.modelCode = modelCode;
        this.responseCode = failed;
        this.errorMessage = message;
    }
}
