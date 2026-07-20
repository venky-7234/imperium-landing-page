package com.imperium.ims.controller;

import com.imperium.ims.dto.ChartDataResponse;
import com.imperium.ims.dto.DashboardSummaryResponse;
import com.imperium.ims.service.DashboardService;
import com.imperium.ims.dto.ApplicationResponse;
import com.imperium.ims.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Admin dashboard metrics and charts")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @org.springframework.cache.annotation.Cacheable(value = "dashboardSummary", key = "#eventId == null ? 'all' : #eventId.toString()")
    @Operation(summary = "Get high-level metric counts for the dashboard cards")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getSummary(@org.springframework.web.bind.annotation.RequestParam(required = false) java.util.UUID eventId) {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getDashboardSummary(eventId)));
    }

    @GetMapping("/recent-applications")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "Get the 5 most recent guest applications")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getRecentApplications(@org.springframework.web.bind.annotation.RequestParam(required = false) java.util.UUID eventId) {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getRecentApplications(eventId)));
    }

    @GetMapping("/charts")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @org.springframework.cache.annotation.Cacheable(value = "dashboardCharts", key = "#eventId == null ? 'all' : #eventId.toString()")
    @Operation(summary = "Get time-series data for the last 7 days of activity")
    public ResponseEntity<ApiResponse<ChartDataResponse>> getCharts(
            @org.springframework.web.bind.annotation.RequestParam(required = false) java.util.UUID eventId,
            java.security.Principal principal) {
        String email = com.imperium.ims.util.SecurityUtils.getCurrentUser()
                .map(com.imperium.ims.security.UserPrincipal::getEmail)
                .orElseThrow(() -> new com.imperium.ims.exception.BusinessException("User not authenticated"));
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getChartData(eventId, email)));
    }

    @GetMapping("/admin-summary")
    @PreAuthorize("hasRole('ADMIN')")
    @org.springframework.cache.annotation.Cacheable(value = "adminDashboardSummary", key = "#principal.getName()")
    @Operation(summary = "Get admin-specific dashboard overview metrics")
    public ResponseEntity<ApiResponse<com.imperium.ims.dto.AdminDashboardStatsResponse>> getAdminSummary(java.security.Principal principal) {
        String email = com.imperium.ims.util.SecurityUtils.getCurrentUser()
                .map(com.imperium.ims.security.UserPrincipal::getEmail)
                .orElseThrow(() -> new com.imperium.ims.exception.BusinessException("User not authenticated"));
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getAdminDashboardSummary(email)));
    }

    @GetMapping("/user-metrics")
    @org.springframework.cache.annotation.Cacheable(value = "userDashboardSummary", key = "#principal.getName()")
    @Operation(summary = "Get metrics for the User dashboard")
    public ResponseEntity<ApiResponse<com.imperium.ims.dto.UserDashboardMetricsResponse>> getUserSummary(java.security.Principal principal) {
        String email = com.imperium.ims.util.SecurityUtils.getCurrentUser()
                .map(com.imperium.ims.security.UserPrincipal::getEmail)
                .orElseThrow(() -> new com.imperium.ims.exception.BusinessException("User not authenticated"));
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getUserDashboardSummary(email)));
    }
}
