package com.imperium.ims.controller;

import com.imperium.ims.dto.EventAnalyticsResponse;
import com.imperium.ims.service.AnalyticsService;
import com.imperium.ims.dto.ApiResponse;
import com.imperium.ims.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for analytics and reporting.
 *
 * <p>Base URL: {@code /api/analytics}
 */
@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Event and organizer analytics APIs")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/events/{eventId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get RSVP analytics for a specific event")
    public ResponseEntity<ApiResponse<EventAnalyticsResponse>> getEventAnalytics(
            @PathVariable Long eventId) {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getEventAnalytics(eventId)));
    }

    @GetMapping("/dashboard")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get dashboard summary for the current organizer")
    public ResponseEntity<ApiResponse<EventAnalyticsResponse>> getDashboard(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                analyticsService.getOrganizerDashboard(principal.getId())));
    }
}
