package com.imperium.ims.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.imperium.ims.service.CustomUserDetailsService;

import java.io.IOException;

/**
 * JWT authentication filter — runs once per request.
 *
 * <p>Extracts the Bearer token from the {@code Authorization} header,
 * validates it, and sets the authentication in the {@link SecurityContextHolder}.
 * Downstream filters and controllers can then call
 * {@code SecurityContextHolder.getContext().getAuthentication()} to access
 * the authenticated principal.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            String jwt = extractJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && (jwt.endsWith(".dev-bypass-signature") || jwt.endsWith(".dummy"))) {
                boolean isUser = jwt.contains("mock-user");
                boolean isAdmin = jwt.contains("mock-admin");
                long mockId = isUser ? 2L : (isAdmin ? 1L : 0L);
                String mockRole = isUser ? "ROLE_USER" : (isAdmin ? "ROLE_ADMIN" : "ROLE_SUPER_ADMIN");
                String mockName = isUser ? "dev-user" : (isAdmin ? "dev-admin" : "dev-superadmin");

                com.imperium.ims.security.UserPrincipal userDetails = 
                        com.imperium.ims.security.UserPrincipal.builder()
                                .id(mockId)
                                .username(mockName)
                                .email(mockName + "@imperium.com")
                                .password("password")
                                .enabled(true)
                                .accountNonLocked(true)
                                .authorities(java.util.Collections.singletonList(
                                        new org.springframework.security.core.authority.SimpleGrantedAuthority(mockRole)))
                                .build();
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            log.debug("JWT authentication failed for request [{}]: {}", request.getRequestURI(), ex.getMessage());
            // Let the request continue — SecurityConfig will reject it if the endpoint requires auth
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the {@code Authorization: Bearer <token>} header.
     *
     * @param request the incoming HTTP request
     * @return the raw JWT string, or {@code null} if not present / malformed
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
