package com.imperium.ims.controller;

import com.imperium.ims.dto.ApplicationResponse;
import com.imperium.ims.dto.ApplicationSearchRequest;
import com.imperium.ims.service.ApplicationService;
import com.imperium.ims.dto.ApiResponse;
import com.imperium.ims.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for Guest Application management.
 *
 * <p>Base URL: {@code /api/applications}
 */
@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
@Tag(name = "Applications", description = "Guest event application management")
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    @Operation(summary = "Submit a new guest application")
    public ResponseEntity<ApiResponse<ApplicationResponse>> createApplication(
            @jakarta.validation.Valid @RequestBody com.imperium.ims.dto.CreateApplicationRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Application submitted successfully.",
                applicationService.createApplication(request)));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SUPER_ADMIN')")
    @Operation(summary = "List all guest applications with pagination")
    public ResponseEntity<ApiResponse<PageResponse<ApplicationResponse>>> getAllApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return ResponseEntity.ok(ApiResponse.success(applicationService.getAllApplications(pageable)));
    }

    @GetMapping("/{publicId}")
    @Operation(summary = "Get application details by ID")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN') or @securityService.isEventOwner(#publicId, principal.username)")
    public ResponseEntity<ApiResponse<ApplicationResponse>> getApplication(@PathVariable UUID publicId) {
        return ResponseEntity.ok(ApiResponse.success(applicationService.getApplicationById(publicId)));
    }

    @GetMapping("/my")
    @Operation(summary = "Get the authenticated user's application")
    public ResponseEntity<ApiResponse<ApplicationResponse>> getMyApplication(java.security.Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(applicationService.getMyApplication(principal.getName())));
    }

    @GetMapping("/{publicId}/guest-profile")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SUPER_ADMIN')")
    @Operation(summary = "Get comprehensive 360-degree profile of a guest based on their application")
    public ResponseEntity<ApiResponse<com.imperium.ims.dto.GuestProfileResponse>> getGuestProfile(@PathVariable UUID publicId) {
        return ResponseEntity.ok(ApiResponse.success(applicationService.getGuestProfile(publicId)));
    }

    @PostMapping("/{publicId}/assign")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Assign an admin to an application")
    public ResponseEntity<ApiResponse<ApplicationResponse>> assignAdmin(
            @PathVariable UUID publicId,
            @jakarta.validation.Valid @RequestBody com.imperium.ims.dto.AssignAdminRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Admin assigned successfully", applicationService.assignAdmin(publicId, request)));
    }

    @PostMapping("/bulk-approve")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @Operation(summary = "Approve multiple applications")
    public ResponseEntity<ApiResponse<java.util.List<ApplicationResponse>>> bulkApprove(
            @jakarta.validation.Valid @RequestBody com.imperium.ims.dto.BulkActionRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Applications approved successfully", applicationService.bulkApprove(request)));
    }

    @PostMapping("/bulk-reject")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @Operation(summary = "Reject multiple applications")
    public ResponseEntity<ApiResponse<java.util.List<ApplicationResponse>>> bulkReject(
            @jakarta.validation.Valid @RequestBody com.imperium.ims.dto.BulkActionRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Applications rejected successfully", applicationService.bulkReject(request)));
    }

    @PostMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'SUPER_ADMIN', 'USER')")
    @Operation(summary = "Search and filter guest applications")
    public ResponseEntity<ApiResponse<PageResponse<ApplicationResponse>>> searchApplications(
            @RequestBody ApplicationSearchRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(ApiResponse.success(applicationService.searchApplications(request, pageable)));
    }

    @PostMapping("/{publicId}/approve")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @Operation(summary = "Approve a guest application and generate an invitation")
    public ResponseEntity<ApiResponse<ApplicationResponse>> approveApplication(@PathVariable UUID publicId) {
        return ResponseEntity.ok(ApiResponse.success("Application approved and invitation sent.",
                applicationService.approveApplication(publicId)));
    }

    @PostMapping("/{publicId}/reject")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'USER')")
    @Operation(summary = "Reject a guest application")
    public ResponseEntity<ApiResponse<ApplicationResponse>> rejectApplication(
            @PathVariable UUID publicId,
            @jakarta.validation.Valid @RequestBody com.imperium.ims.dto.RejectApplicationRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Application rejected.",
                applicationService.rejectApplication(publicId, request)));
    }
}
