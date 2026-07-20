package com.imperium.ims.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Response DTO for {@link com.imperium.ims.entity.Role} — safe to expose over the API.
 */
@Data
public class RoleResponse {

    private Long id;
    private String name;
    private String description;
    private boolean systemRole;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
