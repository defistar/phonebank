package com.store.phonebank.entity;

import jakarta.persistence.PrePersist;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Data
@Table("phone")
public class PhoneEntity implements Persistable<String> {
    @Id
    @Column("id")
    @NotNull
    private String id;

    @NotNull
    @Column("brand_name")
    private String brandName;

    @NotNull
    @Column("model_name")
    private String modelName;

    @NotNull
    @Column("model_code")
    private String modelCode;

    @NotNull
    @Column("phone_count")
    private int phoneCount;

    @NotNull
    @Column("available_count")
    private int availableCount;

    @Transient
    private List<PhoneBookingEntity> bookings = new ArrayList<>();

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Override
    public boolean isNew() {
        return id == null;
    }

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = new UUID(0, 0).toString();
        }
    }
}