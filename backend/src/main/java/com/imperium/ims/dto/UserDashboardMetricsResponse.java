package com.imperium.ims.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDashboardMetricsResponse {
    private String eventName;
    private String welcomeMessage;
    private String invitationStatusBadge;
    private String applicationStatus;
    private long unreadNotifications;
    private long daysRemaining;
}
