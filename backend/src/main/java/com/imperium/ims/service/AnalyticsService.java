package com.imperium.ims.service;

import com.imperium.ims.dto.EventAnalyticsResponse;

/**
 * Service contract for analytics and reporting.
 */
public interface AnalyticsService {

    /**
     * Returns a full RSVP analytics summary for a given event.
     *
     * @param eventId the event to analyse
     * @return the analytics response
     */
    EventAnalyticsResponse getEventAnalytics(Long eventId);

    /**
     * Returns a high-level dashboard summary across all events
     * for the specified organizer.
     *
     * @param organizerId the organizer's user ID
     * @return a summary analytics object
     */
    EventAnalyticsResponse getOrganizerDashboard(Long organizerId);
}
