package com.imperium.ims.service;

import com.imperium.ims.exception.ResourceNotFoundException;
import com.imperium.ims.repository.UserRepository;
import com.imperium.ims.entity.User;
import com.imperium.ims.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Security {@link UserDetailsService} implementation.
 *
 * <p>Loads the {@link User} entity by username or email and converts it
 * to a {@link UserPrincipal} for use in the security context.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Loads a user by username or email address.
     *
     * @param usernameOrEmail the login credential (username or email)
     * @return the populated {@link UserDetails}
     * @throws UsernameNotFoundException if no matching user exists
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username or email: " + usernameOrEmail));

        List<String> roles = user.getRoles().stream()
                .map(role -> "ROLE_" + role.getName())
                .toList();

        return UserPrincipal.of(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                roles,
                user.isEnabled()
        );
    }

    /**
     * Loads a user by their database ID — used in token refresh flows.
     *
     * @param userId the user's primary key
     * @return the populated {@link UserDetails}
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        List<String> roles = user.getRoles().stream()
                .map(role -> "ROLE_" + role.getName())
                .toList();

        return UserPrincipal.of(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                roles,
                user.isEnabled()
        );
    }
}
