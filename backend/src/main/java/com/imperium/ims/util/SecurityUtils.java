package com.imperium.ims.util;

import com.imperium.ims.security.UserPrincipal;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Utility for accessing the current authenticated user from the security context.
 */
@UtilityClass
public class SecurityUtils {

    /**
     * Returns the currently authenticated {@link UserPrincipal}, or empty if
     * the user is not authenticated (anonymous).
     *
     * @return optional user principal
     */
    public static Optional<UserPrincipal> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserPrincipal)) {
            return Optional.empty();
        }
        return Optional.of((UserPrincipal) auth.getPrincipal());
    }

    /**
     * Returns the current user's username, or {@code "SYSTEM"} if unauthenticated.
     *
     * @return username string
     */
    public static String getCurrentUsername() {
        return getCurrentUser()
                .map(UserPrincipal::getUsername)
                .orElse("SYSTEM");
    }

    /**
     * Returns the current user's ID, or {@code null} if unauthenticated.
     *
     * @return user ID or null
     */
    public static Long getCurrentUserId() {
        return getCurrentUser().map(UserPrincipal::getId).orElse(null);
    }

    /**
     * Returns {@code true} if the current user holds the specified role.
     *
     * @param role the role to check (without "ROLE_" prefix)
     * @return true if the user has the role
     */
    public static boolean hasRole(String role) {
        return getCurrentUser()
                .map(user -> user.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_" + role)))
                .orElse(false);
    }
}
