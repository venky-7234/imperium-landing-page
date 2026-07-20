package com.imperium.ims.invitations.validator;

import com.imperium.ims.entity.Invitation;
import com.imperium.ims.entity.InvitationStatus;
import com.imperium.ims.repository.InvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Custom validator for invitation-specific business rules.
 */
@Component
@RequiredArgsConstructor
public class InvitationValidator {

    private final InvitationRepository invitationRepository;

    /**
     * Validates that the invitation is in a state that allows being sent.
     *
     * @param invitation the invitation to check
     * @throws com.imperium.ims.exception.BusinessException if already sent/cancelled/expired
     */
    public void validateCanSend(Invitation invitation) {
        // TODO: Implement — only DRAFT or VIEWED can be (re)sent
    }

    /**
     * Validates that the RSVP token is valid and not expired.
     *
     * @param invitation the invitation found by token
     * @throws com.imperium.ims.exception.BusinessException if expired or already responded
     */
    public void validateRsvpToken(Invitation invitation) {
        // TODO: Implement — check expiresAt, status not CANCELLED/EXPIRED
    }

    /**
     * Validates that the event's guest capacity would not be exceeded.
     *
     * @param eventId          the event to check
     * @param additionalGuests optional extra attendees from the RSVP
     * @throws com.imperium.ims.exception.BusinessException if at capacity
     */
    public void validateCapacity(Long eventId, int additionalGuests) {
        // TODO: Implement
    }

    /**
     * Validates that a duplicate invitation does not already exist for the
     * same guest and event combination.
     *
     * @param eventId    the target event
     * @param guestEmail the guest email
     * @throws com.imperium.ims.exception.DuplicateResourceException if exists
     */
    public void validateNoDuplicate(Long eventId, String guestEmail) {
        // TODO: Implement
    }
}
