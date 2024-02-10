package com.store.phonebank.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class PhoneBookingDto {

    private UUID id;

    private UUID phoneEntityId;

    private String userName;

    private LocalDateTime bookingTime;

    private boolean isReturned = false;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}