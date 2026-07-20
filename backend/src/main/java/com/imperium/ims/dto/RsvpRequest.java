package com.imperium.ims.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request DTO for submitting an RSVP response via the public invitation link.
 */
@Data
public class RsvpRequest {

    @NotNull(message = "RSVP response is required")
    private RsvpResponse response;   // CONFIRMED, DECLINED, TENTATIVE

    private String responseNote;

    private Integer additionalGuests;
}
