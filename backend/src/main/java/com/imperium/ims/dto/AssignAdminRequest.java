package com.imperium.ims.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class AssignAdminRequest {
    @NotBlank(message = "Admin ID or Email is required")
    private String adminIdentifier; // Can be email or UUID
}
