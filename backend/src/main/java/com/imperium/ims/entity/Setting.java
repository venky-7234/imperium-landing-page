package com.imperium.ims.entity;

import com.imperium.ims.entity.BaseEntity;
import com.imperium.ims.common.enums.Status;
import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a global system setting.
 */
@Entity
@Table(name = "settings", uniqueConstraints = {
        @UniqueConstraint(columnNames = "setting_key", name = "uk_settings_key")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Setting extends BaseEntity {

    @Column(name = "setting_key", nullable = false, length = 100)
    private String settingKey;

    @Column(name = "setting_value", nullable = false, length = 1000)
    private String settingValue;

    @Column(name = "description", length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private Status status = Status.ACTIVE;
}
