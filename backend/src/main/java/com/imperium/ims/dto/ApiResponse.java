package com.imperium.ims.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Unified API response envelope for all endpoints.
 *
 * <p>Every controller response is wrapped in this object to provide a
 * consistent contract to API consumers:
 * <pre>
 * {
 *   "success": true,
 *   "message": "Operation completed",
 *   "data": { ... },
 *   "timestamp": "2024-01-01T00:00:00"
 * }
 * </pre>
 *
 * @param <T> the type of the payload data
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;
    private final String errorCode;

    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();

    // ── Static factory helpers ──────────────────────────────────

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("Success")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errorCode(errorCode)
                .build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return error(message, null);
    }
}
