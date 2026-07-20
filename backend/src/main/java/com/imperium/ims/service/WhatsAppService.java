package com.imperium.ims.service;

/**
 * Service contract for outbound WhatsApp message dispatch.
 *
 * <p>Supports both Twilio and Meta Cloud API providers — the active provider
 * is determined by {@code app.whatsapp.provider} in application.yml.
 */
public interface WhatsAppService {

    /**
     * Sends a plain text WhatsApp message.
     *
     * @param toNumber  recipient phone (E.164 format, e.g. +14155238886)
     * @param message   the text body
     */
    void sendMessage(String toNumber, String message);

    /**
     * Sends a WhatsApp invitation message for the specified invitation.
     *
     * @param invitationId the invitation ID
     */
    void sendInvitationMessage(Long invitationId);

    /**
     * Sends a WhatsApp event cancellation notice.
     *
     * @param invitationId the invitation whose guest should be notified
     */
    void sendCancellationMessage(Long invitationId);

    /**
     * Handles an inbound status webhook from the WhatsApp provider
     * (e.g. DELIVERED, READ, FAILED).
     *
     * @param providerMessageId the provider's message ID
     * @param newStatus         the new delivery status
     */
    void handleDeliveryStatusWebhook(String providerMessageId, String newStatus);
}
