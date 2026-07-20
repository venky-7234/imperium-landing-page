package com.imperium.ims.users.validator;

import com.imperium.ims.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Custom validator for user-specific business rules.
 */
@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserRepository userRepository;

    /**
     * Validates that the proposed username is not already taken.
     *
     * @param username the username to check
     * @throws com.imperium.ims.exception.DuplicateResourceException if taken
     */
    public void validateUniqueUsername(String username) {
        // TODO: Implement
    }

    /**
     * Validates that the proposed email address is not already registered.
     *
     * @param email the email to check
     * @throws com.imperium.ims.exception.DuplicateResourceException if taken
     */
    public void validateUniqueEmail(String email) {
        // TODO: Implement
    }

    /**
     * Validates password strength according to the system's password policy.
     *
     * @param password the raw password to validate
     * @throws com.imperium.ims.exception.BusinessException if too weak
     */
    public void validatePasswordStrength(String password) {
        // TODO: Implement — length, uppercase, digit, special char requirements
    }
}
