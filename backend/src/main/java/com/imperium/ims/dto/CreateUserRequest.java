package com.imperium.ims.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Set;

/**
 * Request DTO for creating a new user.
 */
@Data
public class CreateUserRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    @Size(max = 150)
    private String email;

    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;

    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Phone number is invalid")
    private String phoneNumber;

    /** Role IDs to assign to the new user. */
    private Set<Long> roleIds;

    /** Public IDs of events assigned to this user (for admins). */
    private Set<java.util.UUID> assignedEventIds;

}
