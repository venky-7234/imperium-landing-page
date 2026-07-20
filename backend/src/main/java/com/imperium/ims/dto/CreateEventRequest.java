package com.imperium.ims.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class CreateEventRequest {
    @NotBlank(message = "Event title is required")
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
    private String eventType;
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
}
