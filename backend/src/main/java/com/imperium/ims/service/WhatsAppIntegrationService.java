package com.imperium.ims.service;

/**
 * Extension Point: Future WhatsApp Integration
 * 
 * This interface is prepared for upcoming WhatsApp Business API integration.
 * It will support sending event tickets, QR codes, and automated alerts via WhatsApp.
 */
public interface WhatsAppIntegrationService {
    
    /**
     * Send an invitation ticket with QR code via WhatsApp.
     */
    void sendWhatsAppInvitation(String phoneNumber, Long invitationId);
    
    /**
     * Listen for incoming WhatsApp messages or status updates.
     */
    void handleIncomingWebhook(String payload);
}
