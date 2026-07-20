package com.imperium.ims.service;

import com.imperium.ims.dto.ChartDataResponse;
import com.imperium.ims.dto.DashboardSummaryResponse;
import com.imperium.ims.dto.ApplicationResponse;

import java.util.List;

public interface DashboardService {

    DashboardSummaryResponse getDashboardSummary(java.util.UUID eventId);

    List<ApplicationResponse> getRecentApplications(java.util.UUID eventId);

    ChartDataResponse getChartData(java.util.UUID eventId, String email);

    com.imperium.ims.dto.AdminDashboardStatsResponse getAdminDashboardSummary(String adminEmail);
    com.imperium.ims.dto.UserDashboardMetricsResponse getUserDashboardSummary(String userEmail);
}
