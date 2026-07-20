package com.imperium.ims.dto;

import com.imperium.ims.common.enums.Status;
import com.imperium.ims.entity.EventType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Response DTO for an event.
 */
@Data
public class EventResponse {

    private java.util.UUID publicId;
    private String title;
    private String description;
    private String venue;
    private String venueAddress;
    private String city;
    private String country;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Integer maxGuests;
    private LocalDateTime rsvpDeadline;
    private Status status;
    private EventType eventType;
    private String coverImageUrl;
    private boolean isPublic;

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

    // Organizer summary
    private Long organizerId;
    private String organizerName;

    // Stats (populated by service)
    private Long totalInvitations;
    private Long confirmedGuests;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
