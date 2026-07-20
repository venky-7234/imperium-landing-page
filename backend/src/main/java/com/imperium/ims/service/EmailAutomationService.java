package com.imperium.ims.service;

/**
 * Extension Point: Future Gmail Automation
 * 
 * This interface is prepared for upcoming Gmail integration.
 * It will support reading automated replies, parsing attendee responses,
 * and intelligent email thread management.
 */
public interface EmailAutomationService {
    
    /**
     * Connect to Gmail API and fetch new replies.
     */
    void processIncomingEmails();
    
    /**
     * Send intelligent automated reminders.
     */
    void sendEventReminders(Long eventId);
}
