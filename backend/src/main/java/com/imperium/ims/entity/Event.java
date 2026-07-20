package com.imperium.ims.entity;

import com.imperium.ims.entity.BaseEntity;
import com.imperium.ims.common.enums.Status;
import com.imperium.ims.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents an event in the Imperium system (e.g. wedding, conference, gala).
 *
 * <p>Events are the parent container for {@link com.imperium.ims.entity.Invitation}s.
 */
@Entity
@Table(name = "events", indexes = {
        @Index(name = "idx_event_status", columnList = "status"),
        @Index(name = "idx_event_public_id", columnList = "public_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event extends BaseEntity {

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "venue", length = 300)
    private String venue;

    @Column(name = "venue_address", length = 500)
    private String venueAddress;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time")
    private LocalDateTime endDateTime;

    @Column(name = "max_guests")
    private Integer maxGuests;

    @Column(name = "rsvp_deadline")
    private LocalDateTime rsvpDeadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private Status status = Status.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", length = 50)
    private EventType eventType;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    @Column(name = "theme", length = 255)
    private String theme;

    @Column(name = "landing_page_url", length = 500)
    private String landingPageUrl;

    @Column(name = "application_form_url", length = 500)
    private String applicationFormUrl;

    @Column(name = "registration_start")
    private LocalDateTime registrationStart;

    @Column(name = "registration_end")
    private LocalDateTime registrationEnd;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "banner_url", length = 500)
    private String bannerUrl;

    @Column(name = "invitation_template", columnDefinition = "TEXT")
    private String invitationTemplate;

    @Column(name = "email_template", columnDefinition = "TEXT")
    private String emailTemplate;

    @Column(name = "whatsapp_template", columnDefinition = "TEXT")
    private String whatsappTemplate;

    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private boolean isPublic = false;

    /** The user who created / owns this event. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;
}
