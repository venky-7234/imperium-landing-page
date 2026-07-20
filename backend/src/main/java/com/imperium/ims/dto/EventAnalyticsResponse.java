package com.imperium.ims.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Aggregated analytics summary for a single event.
 */
@Data
@Builder
public class EventAnalyticsResponse {

    private Long eventId;
    private String eventTitle;

    private long totalInvitations;
    private long sent;
    private long viewed;
    private long confirmed;
    private long declined;
    private long tentative;
    private long expired;
    private long cancelled;

    /** Response rate = (confirmed + declined + tentative) / sent */
    private double responseRate;

    /** Acceptance rate = confirmed / (confirmed + declined) */
    private double acceptanceRate;

    /** Status breakdown keyed by status name. */
    private Map<String, Long> statusBreakdown;

    /** Channel breakdown keyed by channel name. */
    private Map<String, Long> channelBreakdown;
}
