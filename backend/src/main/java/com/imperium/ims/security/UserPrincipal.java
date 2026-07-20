package com.imperium.ims.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;

/**
 * Immutable representation of the authenticated user within the Spring Security context.
 *
 * <p>Constructed by {@link CustomUserDetailsService} from the {@code User} entity
 * and stored in the {@link org.springframework.security.core.context.SecurityContextHolder}.
 */
@Getter
@Builder
@EqualsAndHashCode(of = "id")
public class UserPrincipal implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Long id;
    private final String username;
    private final String email;

    @JsonIgnore
    private final String password;

    private final boolean enabled;
    private final boolean accountNonLocked;
    private final Collection<? extends GrantedAuthority> authorities;

    /**
     * Convenience factory method — wraps a list of role strings into
     * {@link SimpleGrantedAuthority} instances.
     *
     * @param id       user identifier
     * @param username login username
     * @param email    user email
     * @param password encoded password hash
     * @param roles    list of role names (e.g. "ROLE_ADMIN", "ROLE_USER")
     * @param enabled  whether the account is enabled
     * @return a fully constructed {@link UserPrincipal}
     */
    public static UserPrincipal of(Long id, String username, String email,
                                   String password, List<String> roles, boolean enabled) {
        List<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .map(a -> (GrantedAuthority) a)
                .toList();

        return UserPrincipal.builder()
                .id(id)
                .username(username)
                .email(email)
                .password(password)
                .authorities(authorities)
                .enabled(enabled)
                .accountNonLocked(true)
                .build();
    }

    // ── UserDetails overrides ──────────────────────────────────────────────

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
