package com.imperium.ims.entity;

import com.imperium.ims.entity.BaseEntity;
import com.imperium.ims.common.enums.Channel;
import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a notification dispatched to a user or guest via any channel.
 */
@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {

    @Column(name = "recipient_id", length = 150)
    private String recipientId;       // User ID or guest email

    @Column(name = "recipient_type", length = 30)
    private String recipientType;     // USER, GUEST

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 20)
    private Channel channel;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private boolean read = false;

    @Column(name = "reference_id", length = 100)
    private String referenceId;

    @Column(name = "reference_type", length = 50)
    private String referenceType;

    @Column(name = "notification_type", length = 50)
    private String notificationType;  // INVITATION_SENT, RSVP_RECEIVED, EVENT_CANCELLED, etc.

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private com.imperium.ims.common.enums.Status status = com.imperium.ims.common.enums.Status.ACTIVE;
}
