package com.imperium.ims.entity;

/**
 * Lifecycle status of an invitation.
 */
public enum InvitationStatus {
    /** Invitation has been created. */
    GENERATED,
    /** Invitation has been dispatched to the guest. */
    SENT,
    /** Guest has viewed/opened the invitation. */
    OPENED,
    /** Guest confirmed attendance (RSVP: YES). */
    ACCEPTED,
    /** Guest declined (RSVP: NO). */
    DECLINED,
    /** Guest marked as maybe attending. */
    TENTATIVE,
    /** Invitation expired without a response. */
    EXPIRED,
    /** Invitation was cancelled before being sent or after being sent. */
    CANCELLED
}
