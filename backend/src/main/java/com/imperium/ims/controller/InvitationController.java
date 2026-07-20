package com.imperium.ims.controller;

import com.imperium.ims.dto.ApiResponse;
import com.imperium.ims.dto.PageResponse;
import com.imperium.ims.dto.CreateInvitationRequest;
import com.imperium.ims.dto.InvitationResponse;
import com.imperium.ims.dto.RsvpRequest;
import com.imperium.ims.entity.InvitationStatus;
import com.imperium.ims.service.InvitationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for invitation management and public RSVP.
 *
 * <p>Base URL: {@code /api/invitations}
 */
@RestController
@RequestMapping("/invitations")
@RequiredArgsConstructor
@Tag(name = "Invitations", description = "Invitation creation, sending, and RSVP APIs")
public class InvitationController {

    private final InvitationService invitationService;

    // ── Authenticated endpoints ────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create a single invitation")
    public ResponseEntity<ApiResponse<InvitationResponse>> createInvitation(
            @Valid @RequestBody CreateInvitationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Invitation created", invitationService.createInvitation(request)));
    }

    @PostMapping("/bulk")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create invitations in bulk")
    public ResponseEntity<ApiResponse<List<InvitationResponse>>> createBulk(
            @Valid @RequestBody List<CreateInvitationRequest> requests) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Invitations created",
                        invitationService.createBulkInvitations(requests)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get invitation by ID")
    public ResponseEntity<ApiResponse<InvitationResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(invitationService.getInvitationById(id)));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all invitations (paginated)")
    public ResponseEntity<ApiResponse<PageResponse<InvitationResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, org.springframework.data.domain.Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(
                invitationService.getAllInvitations(pageable)));
    }

    @GetMapping("/event/{eventPublicId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get all invitations for an event (paginated)")
    public ResponseEntity<ApiResponse<PageResponse<InvitationResponse>>> getByEvent(
            @PathVariable java.util.UUID eventPublicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(
                invitationService.getInvitationsByEventPublicId(eventPublicId, pageable)));
    }

    @GetMapping("/event/{eventPublicId}/status/{status}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Filter invitations by event and status")
    public ResponseEntity<ApiResponse<PageResponse<InvitationResponse>>> getByEventAndStatus(
            @PathVariable java.util.UUID eventPublicId,
            @PathVariable InvitationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(
                invitationService.getInvitationsByEventPublicIdAndStatus(eventPublicId, status, pageable)));
    }

    @PostMapping("/{id}/send")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Send a specific invitation")
    public ResponseEntity<ApiResponse<InvitationResponse>> sendInvitation(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Invitation sent", invitationService.sendInvitation(id)));
    }

    @PostMapping("/event/{eventId}/send-all")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Send all pending invitations for an event")
    public ResponseEntity<ApiResponse<Void>> sendAllForEvent(@PathVariable Long eventId) {
        invitationService.sendAllPendingForEvent(eventId);
        return ResponseEntity.ok(ApiResponse.success("All pending invitations dispatched", null));
    }

    @PostMapping("/{id}/resend")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Resend an invitation")
    public ResponseEntity<ApiResponse<Void>> resendInvitation(@PathVariable Long id) {
        invitationService.resendInvitation(id);
        return ResponseEntity.ok(ApiResponse.success("Invitation resent", null));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Cancel an invitation")
    public ResponseEntity<ApiResponse<Void>> cancelInvitation(@PathVariable Long id) {
        invitationService.cancelInvitation(id);
        return ResponseEntity.ok(ApiResponse.success("Invitation cancelled", null));
    }

    @GetMapping("/event/{eventId}/search")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Search guests within an event")
    public ResponseEntity<ApiResponse<PageResponse<InvitationResponse>>> searchGuests(
            @PathVariable Long eventId,
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(
                invitationService.searchGuestsForEvent(eventId, q, pageable)));
    }

    @GetMapping("/{id}/download-pdf")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Download Invitation PDF")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        byte[] pdfBytes = invitationService.downloadInvitationPdf(id);
        
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "invitation-" + id + ".pdf");
        
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    // ── Public RSVP endpoints (no auth required) ─────────────────────────

    @GetMapping("/public/{token}")
    @Operation(summary = "Retrieve invitation details via public token (for guest landing page)")
    public ResponseEntity<ApiResponse<InvitationResponse>> getPublicInvitation(
            @PathVariable String token) {
        return ResponseEntity.ok(ApiResponse.success(invitationService.getInvitationByToken(token)));
    }

    @PostMapping("/rsvp/{token}")
    @Operation(summary = "Submit RSVP response via public token")
    public ResponseEntity<ApiResponse<InvitationResponse>> submitRsvp(
            @PathVariable String token,
            @Valid @RequestBody RsvpRequest request) {
        return ResponseEntity.ok(ApiResponse.success("RSVP submitted",
                invitationService.submitRsvp(token, request)));
    }
}
