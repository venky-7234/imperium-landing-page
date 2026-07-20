package com.imperium.ims.dto;

import com.imperium.ims.common.enums.Status;
import com.imperium.ims.dto.RoleResponse;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Public-facing response DTO for a {@link com.imperium.ims.entity.User}.
 *
 * <p>The {@code password} field is intentionally excluded.
 */
@Data
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String avatarUrl;
    private Status status;
    private boolean enabled;
    private boolean emailVerified;
    private LocalDateTime lastLoginAt;
    private Set<RoleResponse> roles;
    private Set<java.util.UUID> assignedEventIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
