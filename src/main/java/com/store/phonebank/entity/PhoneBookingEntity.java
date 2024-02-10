package com.store.phonebank.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Table("phone_booking")
public class PhoneBookingEntity {

    @Id
    @Column("id")
    @NotNull
    private UUID id;

    @Column("phone_entity_id")
    private UUID phoneEntityId;

    @Column("user_name")
    private String userName;

    @Column("booking_time")
    private LocalDateTime bookingTime;

    @Column("is_returned")
    private boolean isReturned = false;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;
}