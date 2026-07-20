package com.imperium.ims.service;

import com.imperium.ims.dto.EmailRequest;

/**
 * Service contract for outbound email dispatch.
 */
public interface EmailService {

    /**
     * Sends an email asynchronously.
     *
     * @param request the email to send
     */
    void sendEmail(EmailRequest request);

    /**
     * Sends an application confirmation email when a guest submits an application.
     *
     * @param app the application
     */
    void sendApplicationReceived(com.imperium.ims.entity.Application app);

    /**
     * Sends a rejection notice email when a guest's application is rejected.
     *
     * @param app the application
     */
    void sendApplicationRejected(com.imperium.ims.entity.Application app);

    /**
     * Sends an invitation email from a template.
     *
     * @param invitationId the invitation whose details populate the template
     */
    void sendInvitationEmail(Long invitationId);

    /**
     * Sends an event cancellation notice to all confirmed/sent invitees.
     *
     * @param eventId the cancelled event
     */
    void sendCancellationNotice(Long eventId);

    /**
     * Sends a password reset email.
     *
     * @param email the recipient's email
     * @param resetToken the password reset token
     */
    void sendPasswordResetEmail(String email, String resetToken);

    /**
     * Sends an email verification link to a newly registered user.
     *
     * @param email           the recipient's email
     * @param verificationToken the token to embed in the link
     */
    void sendVerificationEmail(String email, String verificationToken);
}
