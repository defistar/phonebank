package com.store.phonebank.entity;

import jakarta.persistence.PrePersist;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Data
@Table("device_info")
public class DeviceInfoEntity implements Persistable<String> {
    @Id
    @Column("id")
    @NotNull
    private String id;

    @NotNull
    @Column("brand_name")
    private String brandName;

    @NotNull
    @Column("model_code")
    private String modelCode;

    @Column("technology")
    private String technology;

    @Column("_2g_bands")
    private String _2g_bands;

    @Column("_3g_bands")
    private String _3g_bands;

    @Column("_4g_bands")
    private String _4g_bands;

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
            System.out.println("id is null");
            id = new UUID(0, 0).toString();
        }
    }
}