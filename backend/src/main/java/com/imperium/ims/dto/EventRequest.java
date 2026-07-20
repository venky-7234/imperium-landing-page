package com.imperium.ims.dto;

import com.imperium.ims.entity.EventType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Request DTO for creating or updating an event.
 */
@Data
public class EventRequest {

    @NotBlank(message = "Event title is required")
    @Size(max = 200)
    private String title;

    private String description;

    @Size(max = 300)
    private String venue;

    @Size(max = 500)
    private String venueAddress;

    @Size(max = 100)
    private String city;

    @Size(max = 100)
    private String country;

    @NotNull(message = "Start date and time is required")
    @Future(message = "Start date must be in the future")
    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    @Min(value = 1, message = "Max guests must be at least 1")
    private Integer maxGuests;

    private LocalDateTime rsvpDeadline;

    private EventType eventType;

    @Size(max = 500)
    private String coverImageUrl;

    private boolean isPublic = false;

    private String theme;
    private String landingPageUrl;
    private String applicationFormUrl;
    private LocalDateTime registrationStart;
    private LocalDateTime registrationEnd;
    private String logoUrl;
    private String bannerUrl;
    private String invitationTemplate;
    private String emailTemplate;
    private String whatsappTemplate;
}
