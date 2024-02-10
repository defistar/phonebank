package com.store.phonebank.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class PhoneBookingDto {

    private String id;

    private String phoneEntityId;

    private String userName;

    private LocalDateTime bookingTime;

    private boolean isReturned = false;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}