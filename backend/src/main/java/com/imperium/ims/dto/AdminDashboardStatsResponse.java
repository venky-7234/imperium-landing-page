package com.imperium.ims.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminDashboardStatsResponse {
    private long assignedGuests;
    private long pendingApplications;
    private long approvedApplications;
    private long rejectedApplications;
    private long generatedInvitations;
    private long todaysApplications;
    private long todaysApprovals;
}
