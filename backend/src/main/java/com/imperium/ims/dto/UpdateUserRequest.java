package com.imperium.ims.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Set;

/**
 * Request DTO for updating an existing user's profile.
 *
 * <p>All fields are optional — only non-null values will be applied (PATCH semantics).
 */
@Data
public class UpdateUserRequest {

    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;

    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Phone number is invalid")
    private String phoneNumber;

    private String avatarUrl;

    /** Role IDs — replaces the user's current roles if provided. */
    private Set<Long> roleIds;

    /** Public IDs of events assigned to this user (for admins). */
    private Set<java.util.UUID> assignedEventIds;

}
