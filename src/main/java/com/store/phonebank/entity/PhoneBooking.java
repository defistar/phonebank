package com.store.phonebank.entity;

import jakarta.persistence.PrePersist;
import lombok.*;
import org.springframework.data.annotation.Id;
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
public class PhoneBooking {

    @Id
    @Column("id")
    @NotNull
    private String id;

    @Column("phone_entity_id")
    private String phoneEntityId;

    @Column("user_id")
    private String userId;

    @Column("booking_time")
    private LocalDateTime bookingTime;

    @Column("returned")
    private boolean returned = false;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = new UUID(0, 0).toString();
        }
    }
}