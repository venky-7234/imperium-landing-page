package com.imperium.ims.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imperium.ims.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Returns a structured 401 JSON response when an unauthenticated user
 * attempts to access a protected resource.
 *
 * <p>Without this, Spring Security defaults to a redirect to a login page,
 * which is incorrect for a REST API.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ApiResponse<Void> body = ApiResponse.error(
                "Authentication required. Please provide a valid JWT token.", "UNAUTHORIZED");
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
