package com.imperium.ims.controller;

import com.imperium.ims.entity.AuditLog;
import com.imperium.ims.service.AuditService;
import com.imperium.ims.dto.ApiResponse;
import com.imperium.ims.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for audit log viewing (ADMIN only).
 *
 * <p>Base URL: {@code /api/audit}
 */
@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
@Tag(name = "Audit", description = "Audit log APIs (admin only)")
public class AuditController {

    private final AuditService auditService;

    @GetMapping("/actor/{actor}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get audit logs by actor")
    public ResponseEntity<ApiResponse<PageResponse<AuditLog>>> getByActor(
            @PathVariable String actor,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                auditService.getLogsByActor(actor, PageRequest.of(page, size))));
    }

    @GetMapping("/resource/{type}/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get audit logs by resource type and ID")
    public ResponseEntity<ApiResponse<PageResponse<AuditLog>>> getByResource(
            @PathVariable String type,
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                auditService.getLogsByResource(type, id, PageRequest.of(page, size))));
    }
}
