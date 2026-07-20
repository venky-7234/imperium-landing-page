package com.imperium.ims.dto;

import com.imperium.ims.common.enums.Channel;
import com.imperium.ims.entity.InvitationStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Response DTO for an invitation.
 */
@Data
public class InvitationResponse {

    private Long id;
    private Long eventId;
    private String eventTitle;

    private String guestName;
    private String guestEmail;
    private String guestPhone;
    private String guestNote;
    private String company;
    private String designation;

    private String invitationNumber;

    private InvitationStatus status;
    private Channel channel;

    private String token;
    private String invitationUrl;    // Full URL for the guest to click

    private LocalDateTime sentAt;
    private LocalDateTime viewedAt;
    private LocalDateTime respondedAt;
    private LocalDateTime expiresAt;

    private String responseNote;
    private Integer additionalGuests;

    private boolean deliveryFailed;
    private String deliveryError;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
