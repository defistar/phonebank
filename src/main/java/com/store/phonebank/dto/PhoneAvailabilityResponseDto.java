package com.store.phonebank.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PhoneAvailabilityResponseDto {
    private String phoneEntityId;
    private String brandName;
    private String modelCode;
    private String availability;
    private LocalDateTime whenLastBooked;
    private String lastBookedUser;
}