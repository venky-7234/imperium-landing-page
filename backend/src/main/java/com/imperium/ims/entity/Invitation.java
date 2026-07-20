package com.imperium.ims.entity;

import com.imperium.ims.entity.BaseEntity;
import com.imperium.ims.common.enums.Channel;
import com.imperium.ims.entity.Event;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Core entity of the Imperium system — represents a single invitation
 * sent to a guest for a specific event.
 *
 * <p>An invitation tracks the full lifecycle from creation to RSVP response
 * across multiple delivery channels (email, WhatsApp, etc.).
 */
@Entity
@Table(name = "invitations", indexes = {
        @Index(name = "idx_invitation_event",  columnList = "event_id"),
        @Index(name = "idx_invitation_email",  columnList = "guest_email"),
        @Index(name = "idx_invitation_token",  columnList = "token", unique = true),
        @Index(name = "idx_invitation_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invitation extends BaseEntity {

    /** The event this invitation belongs to. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    // ── Guest Information ──────────────────────────────────────────────────

    @Column(name = "guest_name", nullable = false, length = 200)
    private String guestName;

    @Column(name = "guest_email", length = 150)
    private String guestEmail;

    @Column(name = "guest_phone", length = 20)
    private String guestPhone;

    @Column(name = "guest_note", length = 500)
    private String guestNote;

    @Column(name = "company", length = 150)
    private String company;

    @Column(name = "designation", length = 150)
    private String designation;

    // ── Invitation State ──────────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private InvitationStatus status = InvitationStatus.GENERATED;

    /** Primary channel used for this invitation. */
    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 20)
    @Builder.Default
    private Channel channel = Channel.EMAIL;

    @Column(name = "invitation_number", length = 50, unique = true)
    private String invitationNumber;

    // ── Unique Token ───────────────────────────────────────────────────────

    /**
     * Cryptographically random token embedded in invitation links.
     * Used for public RSVP endpoints without requiring guest login.
     */
    @Column(name = "token", nullable = false, unique = true, length = 100)
    private String token;

    // ── Timestamps ────────────────────────────────────────────────────────

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "viewed_at")
    private LocalDateTime viewedAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    // ── RSVP Response ────────────────────────────────────────────────────

    @Column(name = "response_note", length = 500)
    private String responseNote;

    @Column(name = "additional_guests")
    private Integer additionalGuests;

    // ── Delivery Tracking ─────────────────────────────────────────────────

    @Column(name = "delivery_id", length = 100)
    private String deliveryId;         // Provider-side message ID

    @Column(name = "delivery_failed", nullable = false)
    @Builder.Default
    private boolean deliveryFailed = false;

    @Column(name = "delivery_error", length = 500)
    private String deliveryError;
}
