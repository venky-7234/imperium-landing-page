package com.imperium.ims.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Response DTO returned on successful authentication.
 */
@Data
@Builder
public class LoginResponse {

    private String accessToken;
    private String refreshToken;

    @Builder.Default
    private String tokenType = "Bearer";

    private Long expiresIn;       // Access token TTL in seconds
    private Long userId;
    private String username;
    private String email;
    private List<String> roles;
}
