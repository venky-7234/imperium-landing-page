package com.imperium.ims.entity;

import com.imperium.ims.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a system role (e.g. ADMIN, MANAGER, STAFF, GUEST).
 *
 * <p>Roles are assigned to {@link com.imperium.ims.entity.User} entities
 * to control access via Spring Security's role-based authorization.
 */
@Entity
@Table(name = "roles", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name", name = "uk_roles_name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends BaseEntity {

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Builder.Default
    @Column(name = "is_system_role", nullable = false)
    private boolean systemRole = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private com.imperium.ims.common.enums.Status status = com.imperium.ims.common.enums.Status.ACTIVE;
}
