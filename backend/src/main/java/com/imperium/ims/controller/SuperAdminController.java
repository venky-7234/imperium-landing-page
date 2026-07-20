package com.imperium.ims.controller;

import com.imperium.ims.entity.AuditLog;
import com.imperium.ims.dto.ApiResponse;
import com.imperium.ims.dto.PageResponse;
import com.imperium.ims.dto.CreateUserRequest;
import com.imperium.ims.dto.UpdateUserRequest;
import com.imperium.ims.dto.UserResponse;
import com.imperium.ims.service.SuperAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * REST controller for Super Admin operations.
 *
 * <p>Base URL: {@code /api/super-admin}
 */
@RestController
@RequestMapping("/super-admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
@Tag(name = "Super Admin", description = "High-level administrative APIs")
public class SuperAdminController {

    private final SuperAdminService superAdminService;

    @PostMapping("/admins")
    @Operation(summary = "Create a new Admin account")
    public ResponseEntity<ApiResponse<UserResponse>> createAdmin(
            @Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Admin created successfully", superAdminService.createAdmin(request)));
    }

    @PostMapping("/users")
    @Operation(summary = "Create a new User account directly")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", superAdminService.createUser(request)));
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "Update an account")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Account updated successfully",
                superAdminService.updateUser(id, request)));
    }

    @PatchMapping("/users/{id}/activate")
    @Operation(summary = "Activate an account")
    public ResponseEntity<ApiResponse<Void>> activateAccount(@PathVariable Long id) {
        superAdminService.activateAccount(id);
        return ResponseEntity.ok(ApiResponse.success("Account activated", null));
    }

    @PatchMapping("/users/{id}/deactivate")
    @Operation(summary = "Deactivate an account")
    public ResponseEntity<ApiResponse<Void>> deactivateAccount(@PathVariable Long id) {
        superAdminService.deactivateAccount(id);
        return ResponseEntity.ok(ApiResponse.success("Account deactivated", null));
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete an account")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        superAdminService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("Account deleted successfully", null));
    }

    @PostMapping("/users/{id}/roles")
    @Operation(summary = "Assign roles/permissions to an account")
    public ResponseEntity<ApiResponse<Void>> assignRoles(
            @PathVariable Long id, @RequestBody Set<Long> roleIds) {
        superAdminService.assignRoles(id, roleIds);
        return ResponseEntity.ok(ApiResponse.success("Roles assigned", null));
    }

    @PatchMapping("/users/{id}/block")
    @Operation(summary = "Block an account")
    public ResponseEntity<ApiResponse<Void>> blockAccount(@PathVariable Long id) {
        superAdminService.blockAccount(id);
        return ResponseEntity.ok(ApiResponse.success("Account blocked", null));
    }

    @PatchMapping("/users/{id}/unblock")
    @Operation(summary = "Unblock an account")
    public ResponseEntity<ApiResponse<Void>> unblockAccount(@PathVariable Long id) {
        superAdminService.unblockAccount(id);
        return ResponseEntity.ok(ApiResponse.success("Account unblocked", null));
    }


    @GetMapping("/users/{id}/login-history")
    @Operation(summary = "View login history for a specific user")
    public ResponseEntity<ApiResponse<PageResponse<com.imperium.ims.entity.LoginHistory>>> getUserLoginHistory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(superAdminService.getUserLoginHistory(id, pageable)));
    }

    @GetMapping("/activities")
    @Operation(summary = "View audit logs for Admin activities")
    public ResponseEntity<ApiResponse<PageResponse<AuditLog>>> getAdminActivities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return ResponseEntity.ok(ApiResponse.success(superAdminService.getAdminActivities(pageable)));
    }
}
