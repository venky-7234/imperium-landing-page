package com.imperium.ims.controller;

import com.imperium.ims.dto.*;
import com.imperium.ims.service.AuthService;
import com.imperium.ims.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication operations.
 *
 * <p>All routes are public (whitelisted in {@link com.imperium.ims.security.SecurityConfig}).
 *
 * <p>Base URL: {@code /api/auth}
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login and token management APIs using Google OAuth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/google")
    @Operation(summary = "Authenticate using Google OAuth 2.0 ID Token")
    public ResponseEntity<ApiResponse<LoginResponse>> googleLogin(
            @Valid @RequestBody GoogleAuthRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Login successful", authService.googleLogin(request)));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh an expired access token")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Token refreshed", authService.refreshToken(request)));
    }

    @PostMapping("/logout")
    @Operation(summary = "Invalidate the current session")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        String token = null;
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        authService.logout(token);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }
}
