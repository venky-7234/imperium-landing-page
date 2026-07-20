package com.imperium.ims.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardSummaryResponse {
    private long pendingApplications;
    private long approvedApplications;
    private long rejectedApplications;
    private long waitlistApplications;
    private long todaysApplications;
    
    private long totalGuests;
    private long activeAdmins;
    private long activeUsers;
    
    private long totalEvents;
    
    private long totalInvitationsSent;
    private long emailCount;
    private long whatsappCount;
    private long todaysInvitations;
}
