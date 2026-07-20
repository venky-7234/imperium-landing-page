package com.imperium.ims.service;

/**
 * Extension Point: Future Advanced Analytics
 * 
 * This interface is prepared for upcoming predictive analytics and machine learning enhancements.
 * It will support attendee behavior predictions, demographic heatmaps, and advanced metrics.
 */
public interface AdvancedAnalyticsService {
    
    /**
     * Generate predictive attendance models based on historical data.
     */
    void generatePredictiveAttendanceModel(Long eventId);
    
    /**
     * Export advanced cross-event metrics.
     */
    void exportCrossEventMetrics();
}
