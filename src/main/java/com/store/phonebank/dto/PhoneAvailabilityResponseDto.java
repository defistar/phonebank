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
public class PhoneAvailabilityResponseDto {
    private UUID phoneEntityId;
    private String brandName;
    private String modelCode;
    private String availability;
    private LocalDateTime whenLastBooked;
    private String lastBookedUser;
}