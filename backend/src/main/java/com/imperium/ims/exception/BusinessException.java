package com.imperium.ims.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a business rule or invariant is violated.
 *
 * <p>Maps to HTTP 422 Unprocessable Entity.
 */
@Getter
@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class BusinessException extends RuntimeException {

    private final String errorCode;

    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(String message) {
        this(message, "BUSINESS_ERROR");
    }
}
