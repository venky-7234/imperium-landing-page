package com.imperium.ims.entity;

import com.imperium.ims.entity.BaseEntity;
import com.imperium.ims.common.enums.Status;
import com.imperium.ims.entity.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import com.imperium.ims.entity.Event;

/**
 * Represents a system user of the Imperium platform.
 *
 * <p>
 * Users are assigned one or more {@link Role} entities which are used
 * by Spring Security to make authorization decisions.
 */
@Entity
@Table(name = "users", uniqueConstraints = {
                @UniqueConstraint(columnNames = "username", name = "uk_users_username"),
                @UniqueConstraint(columnNames = "email", name = "uk_users_email")
}, indexes = {
                @Index(name = "idx_user_status", columnList = "status"),
                @Index(name = "idx_user_public_id", columnList = "public_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

        @Column(name = "username", nullable = false, length = 50)
        private String username;

        @Column(name = "email", nullable = false, length = 150)
        private String email;

        @Column(name = "google_id", length = 255)
        private String googleId;

        @Column(name = "password", nullable = false)
        private String password;

        @Column(name = "first_name", length = 100)
        private String firstName;

        @Column(name = "last_name", length = 100)
        private String lastName;

        @Column(name = "phone_number", length = 20)
        private String phoneNumber;

        @Column(name = "avatar_url")
        private String avatarUrl;

        @Enumerated(EnumType.STRING)
        @Column(name = "status", nullable = false, length = 20)
        @Builder.Default
        private Status status = Status.ACTIVE;

        @Column(name = "enabled", nullable = false)
        @Builder.Default
        private boolean enabled = true;

        @Column(name = "email_verified", nullable = false)
        @Builder.Default
        private boolean emailVerified = false;

        @Column(name = "last_login_at")
        private LocalDateTime lastLoginAt;

        /** Many-to-many relationship with roles — lazy-loaded by default. */
        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
        @Builder.Default
        private Set<Role> roles = new HashSet<>();

        @ManyToMany(fetch = FetchType.LAZY)
        @JoinTable(name = "user_assigned_events", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "event_id"))
        @Builder.Default
        private Set<Event> assignedEvents = new HashSet<>();

}
